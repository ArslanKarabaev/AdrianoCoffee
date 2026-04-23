package com.example.AdrianoCoffee.Dto;

import com.example.AdrianoCoffee.Enum.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userSecondName;
    private String userEmail;
    private Double totalPrice;
    private OrderStatus status;
    private String deliveryAddress;
    private String phone;
    private String comment;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer pointsUsed;
}