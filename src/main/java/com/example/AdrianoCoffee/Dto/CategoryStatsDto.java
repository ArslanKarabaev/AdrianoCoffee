package com.example.AdrianoCoffee.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDto {
    private String category;
    private Long ordersCount;
    private Long totalQuantity;
    private Double totalRevenue;
    private Double averageCheck;
}