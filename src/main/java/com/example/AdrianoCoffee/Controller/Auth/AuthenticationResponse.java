package com.example.AdrianoCoffee.Controller.Auth;

import com.example.AdrianoCoffee.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private  boolean success;
    private  String token;
    private  Long userId;
    private Role role;

}

