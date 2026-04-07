package com.example.AdrianoCoffee.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularDishDto {
    private Long menuItemId;
    private String name;
    private String category;
    private String imageUrl;
    private Long timesOrdered;
    private Long totalQuantity;
    private Double totalRevenue;
    private Double averagePrice;
}