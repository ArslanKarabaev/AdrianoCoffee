package com.example.AdrianoCoffee.Service.UserServices;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.UsersMappingUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsersService {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepo usersRepo;
    public final UsersMappingUtil usersMappingUtil;

    public UsersService(PasswordEncoder passwordEncoder, UsersRepo userRepo, UsersMappingUtil usersMappingUtil) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepo = userRepo;
        this.usersMappingUtil = usersMappingUtil;
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong Password");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepo.save(user);
    }

    @Transactional
    public void updateUser(Long user_id,
                           String firstName,
                           String secondName,
                           LocalDate dateOfBirth,
                           String email,
                           String mobNum) {
        Users users = usersRepo.findById(user_id).orElseThrow(() -> new IllegalStateException(
                "There is no User with id " + user_id));
        if (firstName != null && firstName.length() > 0 && !Objects.equals(users.getFirstName(), firstName)) {
            users.setFirstName(firstName);
        }

        if (email != null && email.length() > 0 && !Objects.equals(users.getEmail(), email)) {
            Optional<Users> usersOptional = usersRepo.findUsersByEmail(email);
            if (usersOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            users.setEmail(email);
        }

        if (secondName != null && secondName.length() > 0 && !Objects.equals(users.getSecondName(), secondName)) {
            users.setSecondName(secondName);
        }

        if (dateOfBirth != null && !Objects.equals(users.getDateOfBirth(), dateOfBirth)) {
            users.setDateOfBirth(dateOfBirth);
        }

        if (mobNum != null && mobNum.length() > 0 && !Objects.equals(users.getMobNum(), mobNum)) {
            users.setMobNum(mobNum);
        }

    }


    public Optional<Users> getUserInfo(Long id) {
        return usersRepo.findById(id);
    }
    public UsersDto getUserInfoDto(Long id) {
        return usersMappingUtil.mapToUsersDto(getUserInfo(id).orElse(new Users()));
    }
}
