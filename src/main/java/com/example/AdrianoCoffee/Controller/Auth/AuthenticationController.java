package com.example.AdrianoCoffee.Controller.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v2/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    // Запрос кода сброса пароля
    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(
            @RequestBody Map<String, String> request) {
        try {
            service.requestPasswordReset(request.get("email"));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Код отправлен на вашу почту"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // Подтверждение кода и установка нового пароля
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, Object>> confirmPasswordReset(
            @RequestBody Map<String, String> request) {
        try {
            service.confirmPasswordReset(
                    request.get("email"),
                    request.get("code"),
                    request.get("newPassword")
            );
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Пароль успешно изменён"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}