package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Service.UserServices.ChangePasswordRequest;
import com.example.AdrianoCoffee.Service.UserServices.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/User")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
public class UsersController {

    private final UsersService service;

    @Operation(
            description = "Change Password endpoint for USER",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PatchMapping(path = "/ChangePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Change Data endpoint for USER",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PutMapping(path = "/UpdateUser/{userId}")
    public void updateUser(
            @PathVariable("userId") Long userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String secondName,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String mobnum){
        service.updateUser(userId,firstName,secondName,dateOfBirth,email,mobnum);
    }

    @GetMapping(path = "/getUserInfo/{userId}")
    public ResponseEntity<UsersDto> getUserInfo(@PathVariable("userId") Long userId) {
        try {
            UsersDto userInfo = service.getUserInfoDto(userId);
            return ResponseEntity.ok(userInfo);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



}
