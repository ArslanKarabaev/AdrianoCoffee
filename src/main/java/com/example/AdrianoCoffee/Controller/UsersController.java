package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.OrderDto;
import com.example.AdrianoCoffee.Dto.UpdateUserRequest;
import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Service.OrderService;
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

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/User")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService service;
    private final OrderService orderService;

    @GetMapping(path = "/me")
    public ResponseEntity<UsersDto> getCurrentUser(Principal connectedUser) {
        try {
            UsersDto userInfo = service.getCurrentUserDto(connectedUser.getName());
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long orderId,
            Principal connectedUser) {
        try {
            // Проверяем что заказ принадлежит пользователю
            String email = connectedUser.getName();
            Long userId = service.getUserIdByEmail(email);
            OrderDto order = orderService.getOrderById(orderId);

            if (!order.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

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
            @RequestBody UpdateUserRequest request) {
        service.updateUser(userId, request.getFirstName(), request.getSecondName(), request.getBirthday(), request.getEmail(), request.getPhone());
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
