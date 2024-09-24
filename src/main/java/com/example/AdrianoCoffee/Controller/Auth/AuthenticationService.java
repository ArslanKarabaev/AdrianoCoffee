package com.example.AdrianoCoffee.Controller.Auth;

import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Service.JwtService.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepo repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Optional<Users> usersByFirstName = repository.findUsersByEmail(request.getEmail());
        if (usersByFirstName.isPresent()) {
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
                .status(true)
                .role(request.getRole())
                .build();
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findUsersByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
