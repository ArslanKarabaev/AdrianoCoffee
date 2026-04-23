package com.example.AdrianoCoffee.Utils;

import com.example.AdrianoCoffee.Dto.CartItemDto;
import com.example.AdrianoCoffee.Dto.OrderDto;
import com.example.AdrianoCoffee.Dto.OrderItemDto;
import com.example.AdrianoCoffee.Entity.Cart;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Entity.Order;
import com.example.AdrianoCoffee.Entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderCartMappingUtil {
    public OrderDto mapToDto(Order order) {
        Users user = order.getUser();

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItemName())
                        .menuItemImage(item.getMenuItemImage())
                        .menuItemPrice(item.getMenuItemPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .userId(user.getUser_id())
                .userFirstName(user.getFirstName())
                .userSecondName(user.getSecondName())
                .userEmail(user.getEmail())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .deliveryAddress(order.getDeliveryAddress())
                .phone(order.getPhone())
                .comment(order.getComment())
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .pointsUsed(order.getPointsUsed() != null ? order.getPointsUsed() : 0)
                .build();
    }

    public CartItemDto mapToDto(Cart cart) {
        Menu menuItem = cart.getMenuItem();
        return CartItemDto.builder()
                .id(cart.getId())
                .menuItemId(menuItem.getId())
                .menuItemName(menuItem.getName())
                .menuItemImage(menuItem.getImageUrl())
                .menuItemPrice(menuItem.getPrice())
                .volume(menuItem.getVolume())
                .quantity(cart.getQuantity())
                .subtotal(menuItem.getPrice() * cart.getQuantity())
                .build();
    }
}
