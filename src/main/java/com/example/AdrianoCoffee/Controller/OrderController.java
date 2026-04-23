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
@RequestMapping("/api/v2/Orders")
public class OrderController {

    private final OrderService orderService;
    private final UsersRepo usersRepo;

    public OrderController(OrderService orderService, UsersRepo usersRepo) {
        this.orderService = orderService;
        this.usersRepo = usersRepo;
    }

    // Получить мои заказы
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyOrders(Authentication authentication) {
        try {
            String email = authentication.getName();
            Users user = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            List<OrderDto> orders = orderService.getUserOrders(user.getUser_id());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Получить заказ по ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(Map.of("success", true, "order", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}