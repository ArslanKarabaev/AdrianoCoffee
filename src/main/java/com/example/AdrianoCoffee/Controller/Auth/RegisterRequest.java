package com.example.AdrianoCoffee.Controller.Auth;

import com.example.AdrianoCoffee.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String secondName;
    private LocalDate dateOfBirth;
    private String email;
    private String mobNum;
    private String password;
    private Role role;
}
