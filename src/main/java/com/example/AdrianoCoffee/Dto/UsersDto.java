package com.example.AdrianoCoffee.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {
    private Long user_id;
    private String firstName;
    private String secondName;
    private LocalDate dateOfBirth;
    private String email;
    private String mobNum;
    private String password;
    private Integer age;

    public UsersDto(String firstName, String secondName, LocalDate dateOfBirth, String email, String mobNum, String password) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.mobNum = mobNum;
        this.password = password;
    }
}
