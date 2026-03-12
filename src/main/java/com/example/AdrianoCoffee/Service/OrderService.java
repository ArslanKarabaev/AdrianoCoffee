package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.OrderDto;
import com.example.AdrianoCoffee.Entity.*;
import com.example.AdrianoCoffee.Enum.OrderStatus;
import com.example.AdrianoCoffee.Repository.CartRepo;
import com.example.AdrianoCoffee.Repository.OrderRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.OrderCartMappingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UsersRepo usersRepo;
    private final OrderCartMappingUtil mappingUtil;

    public OrderService(OrderRepo orderRepo, CartRepo cartRepo, UsersRepo usersRepo, OrderCartMappingUtil mappingUtil) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.usersRepo = usersRepo;
        this.mappingUtil = mappingUtil;
    }

    // Создать заказ из корзины
    @Transactional
    public OrderDto createOrder(Long userId, String deliveryAddress, String phone, String comment) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Получаем корзину
        List<Cart> cartItems = cartRepo.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        // Создаём заказ
        Order order = Order.builder()
                .user(user)
                .deliveryAddress(deliveryAddress)
                .phone(phone)
                .comment(comment)
                .status(OrderStatus.PENDING)
                .build();

        // Добавляем позиции из корзины
        double totalPrice = 0.0;

        for (Cart cartItem : cartItems) {
            Menu menuItem = cartItem.getMenuItem();
            double subtotal = menuItem.getPrice() * cartItem.getQuantity();
            totalPrice += subtotal;

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .menuItemName(menuItem.getName())
                    .menuItemPrice(menuItem.getPrice())
                    .menuItemImage(menuItem.getImageUrl())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalPrice(totalPrice);

        // Сохраняем заказ
        order = orderRepo.save(order);

        // Очищаем корзину
        cartRepo.deleteByUserId(userId);

        return mappingUtil.mapToDto(order);
    }

    // Получить заказы пользователя
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(mappingUtil::mapToDto)
                .collect(Collectors.toList());
    }

    // Получить все заказы (для админа)
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepo.findAllByOrderByCreatedAtDesc();
        return orders.stream()
                .map(mappingUtil::mapToDto)
                .collect(Collectors.toList());
    }

    // Получить заказ по ID
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        return mappingUtil.mapToDto(order);
    }

    // Изменить статус заказа
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        order.setStatus(status);
        order = orderRepo.save(order);

        return mappingUtil.mapToDto(order);
    }

    // Отменить заказ
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }


}