package com.example.AdrianoCoffee.Utils;

import com.example.AdrianoCoffee.Dto.CategoryStatsDto;
import com.example.AdrianoCoffee.Dto.PopularDishDto;
import org.springframework.stereotype.Service;

@Service
public class OrderMappingUtil {

    public PopularDishDto mapToPopularDishDto(Object[] row) {
        return PopularDishDto.builder()
                .menuItemId(((Number) row[0]).longValue())
                .name((String) row[1])
                .category((String) row[2])
                .imageUrl((String) row[3])
                .timesOrdered(((Number) row[4]).longValue())
                .totalQuantity(((Number) row[5]).longValue())
                .totalRevenue(((Number) row[6]).doubleValue())
                .averagePrice(((Number) row[7]).doubleValue())
                .build();
    }

    /**
     * Маппинг Object[] в CategoryStatsDto
     */
    public CategoryStatsDto mapToCategoryStatsDto(Object[] row) {
        return CategoryStatsDto.builder()
                .category((String) row[0])
                .ordersCount(((Number) row[1]).longValue())
                .totalQuantity(((Number) row[2]).longValue())
                .totalRevenue(((Number) row[3]).doubleValue())
                .averageCheck(((Number) row[4]).doubleValue())
                .build();
    }
}
