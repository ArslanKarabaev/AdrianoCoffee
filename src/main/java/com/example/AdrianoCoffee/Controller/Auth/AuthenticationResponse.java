package com.example.AdrianoCoffee.Controller.Auth;

import com.example.AdrianoCoffee.Enum.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthenticationResponse {
    private  boolean success;
    private  String token;
    private  Long userId;
    private Role role;
    private Boolean status;

}

