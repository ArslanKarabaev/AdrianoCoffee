package com.example.AdrianoCoffee.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private Long totalOrders;
    private Double totalRevenue;
    private Double averageCheck;
    private Long activeUsers;

    private List<PopularDishDto> topDishes;
    private PopularDishDto mostPopular;
    private PopularDishDto mostProfitable;

    private List<CategoryStatsDto> categoryStats;

    private Map<Integer, Long> ordersByHour;
    private Map<String, Long> ordersByDayOfWeek;
    private Map<String, Long> ordersByMonth;
}