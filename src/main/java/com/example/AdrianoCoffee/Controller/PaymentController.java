package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Enum.OrderStatus;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Service.BonusService;
import com.example.AdrianoCoffee.Service.OrderService;
import com.example.AdrianoCoffee.Service.Payment.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/Payment")
@RequiredArgsConstructor
public class PaymentController {

    private final StripePaymentService stripePaymentService;
    private final OrderService orderService;
    private final UsersRepo usersRepo;
    private final BonusService bonusService;

    // Создать PaymentIntent с деталями заказа в metadata
    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Long userId = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"))
                    .getUser_id();

            String currency = request.getOrDefault("currency", "usd").toString();
            String deliveryAddress = request.getOrDefault("deliveryAddress", "").toString();
            String phone = request.getOrDefault("phone", "").toString();
            String comment = request.getOrDefault("comment", "").toString();

            int pointsToUse = 0;
            if (request.containsKey("pointsToUse")) {
                pointsToUse = Integer.parseInt(request.get("pointsToUse").toString());
            }

            String clientSecret = stripePaymentService.createPaymentIntentWithDetails(
                    userId, currency, deliveryAddress, phone, comment, pointsToUse
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "clientSecret", clientSecret,
                    "pointsUsed", pointsToUse
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Ручное подтверждение оплаты (без webhook — для локальной разработки)
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String paymentIntentId = request.get("paymentIntentId");
            Long orderId = stripePaymentService.confirmPayment(paymentIntentId);

            // Списываем баллы если были использованы
            String pointsStr = request.get("pointsUsed");
            if (pointsStr != null && Integer.parseInt(pointsStr) > 0) {
                Long userId = usersRepo.findUsersByEmail(authentication.getName())
                        .orElseThrow().getUser_id();
                bonusService.spendPoints(userId, Integer.parseInt(pointsStr), orderId);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Заказ создан",  "orderId", orderId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Отмена заказа с возвратом — вызывается из AdminController
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<Map<String, Object>> refundOrder(
            @PathVariable Long orderId) {
        try {
            // Возврат денег через Stripe
            stripePaymentService.refundPayment(orderId);
            // Меняем статус заказа на CANCELLED
            orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

            return ResponseEntity.ok(Map.of("success", true, "message", "Возврат выполнен, заказ отменён"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}