package com.example.AdrianoCoffee.Config;

import com.example.AdrianoCoffee.Controller.Auth.AuthenticationService;
import com.example.AdrianoCoffee.Controller.Auth.RegisterRequest;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

import static com.example.AdrianoCoffee.Enum.Role.ADMIN;
import static com.example.AdrianoCoffee.Enum.Role.MANAGER;

@Configuration
@RequiredArgsConstructor
public class UsersConfig implements CommandLineRunner {

    private final UsersRepo usersRepo;
    private final AuthenticationService service;

    @Override
    public void run(String... args) throws Exception {
        if (usersRepo.findUsersByEmail("admin@gmail.com").isEmpty()) {
            var admin = RegisterRequest.builder()
                    .firstName("Admin")
                    .secondName("Admin")
                    .email("admin@gmail.com")
                    .password("password")
                    .dateOfBirth(LocalDate.of(2000, 8, 2))
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).getToken());
        }

        if (usersRepo.findUsersByEmail("manager@gmail.com").isEmpty()) {
            var manager = RegisterRequest.builder()
                    .firstName("Manager")
                    .secondName("Manager")
                    .email("manager@gmail.com")
                    .password("pass")
                    .dateOfBirth(LocalDate.of(2000, 8, 2))
                    .role(MANAGER)
                    .build();
            System.out.println("Manager token: " + service.register(manager).getToken());

        }
    }
}
