package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Entity.BonusTransaction;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Service.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/Bonus")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;
    private final UsersRepo usersRepo;

    // Баланс текущего пользователя
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(Map.of("balance", bonusService.getBalance(userId)));
    }

    // История транзакций текущего пользователя
    @GetMapping("/history")
    public ResponseEntity<List<BonusTransaction>> getHistory(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(bonusService.getHistory(userId));
    }

    // Получить userId по QR (для кассира — возвращаем имя и баланс)
    @GetMapping("/qr-info/{userId}")
    public ResponseEntity<Map<String, Object>> getQrInfo(
            @PathVariable Long userId) {
        var user = usersRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "name", user.getFirstName() + " " + user.getSecondName(),
                "balance", user.getBonusPoints()
        ));
    }

    // Начислить баллы вручную (кассир)
    @PostMapping("/manual-credit")
    public ResponseEntity<Map<String, Object>> manualCredit(
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            Double amount = Double.parseDouble(request.get("amount").toString());
            String description = request.getOrDefault("description", "Покупка в филиале").toString();

            bonusService.earnPointsManual(userId, amount, description);
            int newBalance = bonusService.getBalance(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "pointsAdded", (int) Math.floor(amount * 0.05),
                    "newBalance", newBalance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private Long getUserId(Authentication auth) {
        return usersRepo.findUsersByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"))
                .getUser_id();
    }
}