package com.example.AdrianoCoffee.Controller.Auth;

import com.example.AdrianoCoffee.Entity.PasswordResetToken;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.PasswordResetTokenRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Service.EmailService;
import com.example.AdrianoCoffee.Service.JwtService.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepo repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;

    // ───── Регистрация ────────────────────────────────────
    public AuthenticationResponse register(RegisterRequest request) {
        Optional<Users> existing = repository.findUsersByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new IllegalStateException("This user is already registered");
        }

        var user = Users.builder()
                .firstName(request.getFirstName())
                .secondName(request.getSecondName())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .mobNum(request.getMobNum())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(LocalDate.now().getYear() - request.getDateOfBirth().getYear())
                .role(request.getRole())
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .success(true)
                .token(jwtToken)
                .userId(user.getUser_id())
                .role(user.getRole())
                .build();
    }

    // ───── Вход ───────────────────────────────────────────
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findUsersByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .success(true)
                .token(jwtToken)
                .userId(user.getUser_id())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    // ───── Запрос сброса пароля ───────────────────────────
    @Transactional
    public void requestPasswordReset(String email) {
        Users user = repository.findUsersByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Пользователь с таким email не найден"));

        // Удаляем старые коды для этого email
        passwordResetTokenRepo.deleteByEmail(email);

        // Генерируем 6-значный код
        String code = String.format("%06d", new Random().nextInt(999999));

        // Сохраняем в БД
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        passwordResetTokenRepo.save(token);

        // Отправляем письмо
        String customerName = user.getFirstName() != null ? user.getFirstName() : "Пользователь";
        emailService.sendPasswordResetEmail(email, customerName, code);
    }

    // ───── Подтверждение кода и смена пароля ─────────────
    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepo
                .findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new IllegalStateException("Неверный или истёкший код"));

        // Проверяем срок действия
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Код истёк. Запросите новый.");
        }

        // Меняем пароль
        Users user = repository.findUsersByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);

        // Помечаем код как использованный
        token.setUsed(true);
        passwordResetTokenRepo.save(token);
    }
}