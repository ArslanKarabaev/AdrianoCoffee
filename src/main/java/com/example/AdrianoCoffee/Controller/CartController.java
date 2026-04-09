package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.CartItemDto;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/Cart")
public class CartController {

    private final CartService cartService;
    private final UsersRepo usersRepo;

    public CartController(CartService cartService, UsersRepo usersRepo) {
        this.cartService = cartService;
        this.usersRepo = usersRepo;
    }

    // Добавить товар в корзину
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();

            Users user = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            Long userId = user.getUser_id();
            Long menuItemId = Long.valueOf(request.get("menuItemId").toString());
            Integer quantity = (Integer) request.getOrDefault("quantity", 1);

            CartItemDto item = cartService.addToCart(userId, menuItemId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Товар добавлен в корзину");
            response.put("item", item);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Получить корзину
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(Authentication authentication) {
        try {
            String email = authentication.getName();

            Users user = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            Long userId = user.getUser_id();

            List<CartItemDto> items = cartService.getCart(userId);
            Double total = cartService.calculateTotal(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", items);
            response.put("total", total);
            response.put("count", items.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Обновить количество
    @PutMapping("/{cartId}")
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> request
    ) {
        try {
            Integer quantity = request.get("quantity");
            CartItemDto item = cartService.updateQuantity(cartId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("item", item);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Удалить товар
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, Object>> removeItem(@PathVariable Long cartId) {
        try {
            cartService.removeFromCart(cartId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Товар удалён");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Очистить корзину
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(Authentication authentication) {
        try {
            String email = authentication.getName();

            // Найдите пользователя по email
            Users user = usersRepo.findUsersByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            Long userId = user.getUser_id();
            cartService.clearCart(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Корзина очищена");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}