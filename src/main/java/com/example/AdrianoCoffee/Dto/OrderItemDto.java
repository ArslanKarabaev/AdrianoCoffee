package com.example.AdrianoCoffee.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private String menuItemImage;
    private Double menuItemPrice;
    private Integer quantity;
    private Double subtotal;
}