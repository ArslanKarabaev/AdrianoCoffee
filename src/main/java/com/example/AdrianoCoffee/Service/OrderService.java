package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.OrderDto;
import com.example.AdrianoCoffee.Entity.*;
import com.example.AdrianoCoffee.Enum.OrderStatus;
import com.example.AdrianoCoffee.Repository.CartRepo;
import com.example.AdrianoCoffee.Repository.OrderRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.OrderCartMappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UsersRepo usersRepo;
    private final OrderCartMappingUtil mappingUtil;
    private final EmailService emailService;
    private final BonusService bonusService;

    @Transactional
    public OrderDto createOrderAfterPayment(Long userId, String deliveryAddress,
                                            String phone, String comment,
                                            String paymentIntentId, int pointsUsed) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Cart> cartItems = cartRepo.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        Order order = Order.builder()
                .user(user)
                .deliveryAddress(deliveryAddress)
                .phone(phone)
                .comment(comment)
                .status(OrderStatus.PAID)
                .paymentIntentId(paymentIntentId)
                .paymentMethod("card")
                .pointsUsed(pointsUsed)
                .build();

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
        order = orderRepo.save(order);
        cartRepo.deleteByUserId(userId);

        try {
            String customerName = user.getFirstName() != null ? user.getFirstName() : "Гость";
            emailService.sendOrderStatusEmail(
                    user.getEmail(), customerName,
                    order.getId(), OrderStatus.PAID.name(), order.getTotalPrice()
            );
        } catch (Exception e) {
            System.err.println("Ошибка email: " + e.getMessage());
        }

        return mappingUtil.mapToDto(order);
    }

    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(mappingUtil::mapToDto).collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        return orderRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(mappingUtil::mapToDto).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long orderId) {
        return mappingUtil.mapToDto(orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found")));
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        validateStatusTransition(order.getStatus(), newStatus, orderId);

        order.setStatus(newStatus);
        order = orderRepo.save(order);

        if(newStatus == OrderStatus.DELIVERED){
            try {
                bonusService.earnPointsForOrder(
                        order.getUser().getUser_id(),
                        order.getTotalPrice(),
                        order.getId()
                );
            }catch (Exception e){
                System.err.println("Ошибка при начеслении баллов: " + e.getMessage());
            }
        }

        try {
            String customerName = order.getUser().getFirstName() != null
                    ? order.getUser().getFirstName() : "Гость";
            emailService.sendOrderStatusEmail(
                    order.getUser().getEmail(), customerName,
                    order.getId(), newStatus.name(), order.getTotalPrice()
            );
        } catch (Exception e) {
            System.err.println("Ошибка email: " + e.getMessage());
        }

        return mappingUtil.mapToDto(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next, Long orderId) {
        boolean valid = switch (current) {
            case PAID       -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED  -> next == OrderStatus.PREPARING;
            case PREPARING  -> next == OrderStatus.DELIVERING;
            case DELIVERING -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Недопустимый переход: " + current + " → " + next + " для заказа #" + orderId
            );
        }
    }
}