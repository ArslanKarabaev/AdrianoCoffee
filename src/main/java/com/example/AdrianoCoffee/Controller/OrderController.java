package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.OrderDto;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/orders")
@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;
    private final UsersRepo usersRepo;

    public OrderController(OrderService orderService, UsersRepo usersRepo) {
        this.orderService = orderService;
        this.usersRepo = usersRepo;
    }

    // Создать заказ из корзины
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, String> request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();

            // Найдите пользователя по email
            Users user = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            Long userId = user.getUser_id();

            String deliveryAddress = request.get("deliveryAddress");
            String phone = request.get("phone");
            String comment = request.getOrDefault("comment", "");

            OrderDto order = orderService.createOrder(userId, deliveryAddress, phone, comment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Заказ успешно создан");
            response.put("order", order);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Получить мои заказы
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyOrders(Authentication authentication) {
        try {
            String email = authentication.getName();

            // Найдите пользователя по email
            Users user = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            Long userId = user.getUser_id();

            List<OrderDto> orders = orderService.getUserOrders(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Получить заказ по ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Отменить заказ
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.cancelOrder(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Заказ отменён");
            response.put("order", order);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}