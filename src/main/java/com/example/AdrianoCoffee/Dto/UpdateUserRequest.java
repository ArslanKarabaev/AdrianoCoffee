package com.example.AdrianoCoffee.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String secondName;
    private LocalDate birthday;
    private String email;
    private String phone;
}
