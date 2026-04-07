package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.OrderDto;
import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Enum.Category;
import com.example.AdrianoCoffee.Enum.OrderStatus;
import com.example.AdrianoCoffee.Enum.Role;
import com.example.AdrianoCoffee.Service.AdminService;
import com.example.AdrianoCoffee.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/Admin/")
@Tag(name = "Admin")
@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
    }

    @Operation(
            description = "Get All Users endpoint for ADMIN",
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
    @GetMapping(path = "getAllUsers")
    public ResponseEntity<List<UsersDto>> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsersDto());
    }

    @Operation(
            description = "Get User By ID endpoint for ADMIN",
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
    @GetMapping(path = "getUserById/{userId}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(adminService.getUserDtoById(userId));
    }

    @GetMapping(path = "getUserByName")
    public ResponseEntity<?> getUserByName(@RequestParam(required = false) String firstName, @RequestParam(required = false) String secondName){
            // Если оба пустые
            if ((firstName == null || firstName.isEmpty()) &&
                    (secondName == null || secondName.isEmpty())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Укажите имя или фамилию"));
            }

            // Если указаны оба
            if (firstName != null && !firstName.isEmpty() &&
                    secondName != null && !secondName.isEmpty()) {
                UsersDto user = adminService.getUserByNameDto(firstName, secondName);
                return ResponseEntity.ok(user);
            }

            // Если только имя
            if (firstName != null && !firstName.isEmpty()) {
                List<UsersDto> users = adminService.getUsersByFirstName(firstName);
                return ResponseEntity.ok(users);
            }

            // Если только фамилия
            if (secondName != null && !secondName.isEmpty()) {
                List<UsersDto> users = adminService.getUsersBySecondName(secondName);
                return ResponseEntity.ok(users);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Неверные параметры"));
        }

    @GetMapping(path = "getUserById/{userId}/Role")
    public ResponseEntity<Role> getUserRoleById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(adminService.getUsersRoleById(userId));
    }

    @Operation(
            description = "Delete User By ID endpoint for ADMIN",
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
    @DeleteMapping(path = "deleteUser/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("userId") Long userId){
        adminService.deleteUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Пользователь удалён");
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "restoreUser/{userId}")
    public ResponseEntity<Map<String, Object>> restoreUser(@PathVariable("userId") Long userId){
        adminService.restoreUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Пользователь восстановлен");
        return ResponseEntity.ok(response);
    }


    // ===== ВАРИАНТ 1: С ИЗОБРАЖЕНИЕМ (MULTIPART) =====
    @PostMapping(path = "addNewItemToMenu", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> addNewMenuItemWithImage(
            @RequestParam("name") String name,
            @RequestParam("category") Category category,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("volume") String volume,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            // Обработка изображения
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = adminService.saveImage(image);
            }

            // Создание объекта Menu
            Menu menu = new Menu();
            menu.setName(name);
            menu.setCategory(category);
            menu.setDescription(description);
            menu.setPrice(price);
            menu.setVolume(volume);
            menu.setImageUrl(imageUrl);

            // Сохранение в БД
            adminService.addNewItemToMenu(menu);

            // Возвращаем успешный ответ
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Блюдо успешно добавлено");
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Ошибка при добавлении блюда: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ===== ВАРИАНТ 2: БЕЗ ИЗОБРАЖЕНИЯ (JSON) =====
    @PostMapping(path = "addNewItemToMenu", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> addNewMenuItemWithoutImage(@RequestBody Menu menu) {
        try {
            // Сохранение в БД
            adminService.addNewItemToMenu(menu);

            // Возвращаем успешный ответ
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Блюдо успешно добавлено");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Ошибка при добавлении блюда: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(
            description = "Delete Menu Item endpoint for ADMIN",
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
    @DeleteMapping(path = "deleteItemFromMenu/{menuId}")
    public ResponseEntity<Map<String, Object>> deleteMenuItem(@PathVariable("menuId") Long menuId) {
        adminService.deleteItemFromMenu(menuId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Блюдо удалено");
        return ResponseEntity.ok(response);
    }


    // 2. Обновление блюда
    @PutMapping(path = "updateMenuItem/{menuId}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateMenuItem(
            @PathVariable("menuId") Long menuId,
            @RequestParam("name") String name,
            @RequestParam("category") Category category,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam(value = "volume", required = false) String volume,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            adminService.updateMenu(menuId, name, category, description, price, volume, image);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Блюдо успешно обновлено");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Ошибка при обновлении блюда: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);  // ← ДОБАВЬТЕ ЭТУ СТРОКУ!
        }
    }


    @GetMapping(path = "getAllOrders")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        try {
            List<OrderDto> orders = orderService.getAllOrders();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            response.put("count", orders.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Изменить статус заказа
    @PutMapping(path = "updateOrderStatus/{orderId}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request
    ) {
        try {
            OrderStatus status = OrderStatus.valueOf(request.get("status"));
            OrderDto order = orderService.updateOrderStatus(orderId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Статус заказа обновлён");
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