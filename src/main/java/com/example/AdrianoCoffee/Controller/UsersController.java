package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Service.UserServices.ChangePasswordRequest;
import com.example.AdrianoCoffee.Service.UserServices.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/User/ChangePassword")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
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
    @PatchMapping
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
    @PutMapping(path = "updateUser/{userId}")
    public void updateUser(
            @PathVariable("userId") Long userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String secondName,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String mobnum){
        service.updateUser(userId,firstName,secondName,dateOfBirth,email,mobnum);
    }
}
