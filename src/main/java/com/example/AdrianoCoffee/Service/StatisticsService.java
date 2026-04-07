package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.*;
import com.example.AdrianoCoffee.Repository.OrderRepo;
import com.example.AdrianoCoffee.Utils.OrderMappingUtil;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final OrderRepo orderRepo;
    private final OrderMappingUtil orderMappingUtil;

    public StatisticsService(OrderRepo orderRepo, OrderMappingUtil orderMappingUtil) {
        this.orderRepo = orderRepo;
        this.orderMappingUtil = orderMappingUtil;
    }

    /**
     * Получить общую статистику
     */
    public StatisticsDto getGeneralStatistics(LocalDate startDate, LocalDate endDate) {
        StatisticsDto stats = new StatisticsDto();

        // 1. Общая статистика
        stats.setTotalOrders(getTotalOrders(startDate, endDate));
        stats.setTotalRevenue(getTotalRevenue(startDate, endDate));
        stats.setAverageCheck(getAverageCheck(startDate, endDate));
        stats.setActiveUsers(getActiveUsersCount(startDate, endDate));

        // 2. Топ блюд
        stats.setTopDishes(getTopDishes(10, startDate, endDate));
        stats.setMostPopular(getMostPopularDish(startDate, endDate));
        stats.setMostProfitable(getMostProfitableDish(startDate, endDate));

        // 3. Статистика по категориям
        stats.setCategoryStats(getCategoryStatistics(startDate, endDate));

        // 4. Статистика по времени
        stats.setOrdersByHour(getOrdersByHour(startDate, endDate));
        stats.setOrdersByDayOfWeek(getOrdersByDayOfWeek(startDate, endDate));
        stats.setOrdersByMonth(getOrdersByMonth(startDate, endDate));

        return stats;
    }

    // ===== ОБЩАЯ СТАТИСТИКА =====

    /**
     * Общее количество заказов
     */
    private Long getTotalOrders(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT COUNT(*) 
            FROM orders 
            WHERE created_at BETWEEN :startDate AND :endDate
            AND status != 'CANCELLED'
            """;

        // Используйте EntityManager или создайте метод в репозитории
        return orderRepo.countOrdersBetweenDates(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    /**
     * Общая выручка
     */
    private Double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return orderRepo.sumRevenueBetweenDates(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    /**
     * Средний чек
     */
    private Double getAverageCheck(LocalDate startDate, LocalDate endDate) {
        Long totalOrders = getTotalOrders(startDate, endDate);
        if (totalOrders == 0) return 0.0;

        Double totalRevenue = getTotalRevenue(startDate, endDate);
        return totalRevenue / totalOrders;
    }

    /**
     * Количество активных пользователей (сделавших хотя бы 1 заказ)
     */
    private Long getActiveUsersCount(LocalDate startDate, LocalDate endDate) {
        return orderRepo.countDistinctUsersBetweenDates(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    // ===== ТОП БЛЮД =====

    /**
     * Топ N блюд по количеству заказов
     */
    private List<PopularDishDto> getTopDishes(int limit, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepo.findTopDishesRaw(
                limit,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        return results.stream()
                .map(orderMappingUtil::mapToPopularDishDto)
                .collect(Collectors.toList());
    }

    /**
     * Самое популярное блюдо (по количеству заказов)
     */
    private PopularDishDto getMostPopularDish(LocalDate startDate, LocalDate endDate) {
        List<PopularDishDto> topDishes = getTopDishes(1, startDate, endDate);
        return topDishes.isEmpty() ? null : topDishes.get(0);
    }

    /**
     * Самое прибыльное блюдо
     */
    private PopularDishDto getMostProfitableDish(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepo.findMostProfitableDishRaw(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        return results.isEmpty() ? null : orderMappingUtil.mapToPopularDishDto(results.get(0));
    }

    /**
     * Статистика по категориям
     */
    private List<CategoryStatsDto> getCategoryStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepo.findCategoryStatisticsRaw(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        return results.stream()
                .map(orderMappingUtil::mapToCategoryStatsDto)
                .collect(Collectors.toList());
    }

    // ===== СТАТИСТИКА ПО ВРЕМЕНИ =====

    /**
     * Заказы по часам дня
     */
    private Map<Integer, Long> getOrdersByHour(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepo.findOrdersByHour(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        Map<Integer, Long> ordersByHour = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            ordersByHour.put(i, 0L);
        }

        for (Object[] result : results) {
            Integer hour = ((Number) result[0]).intValue();
            Long count = ((Number) result[1]).longValue();
            ordersByHour.put(hour, count);
        }

        return ordersByHour;
    }

    /**
     * Заказы по дням недели
     */
    private Map<String, Long> getOrdersByDayOfWeek(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepo.findOrdersByDayOfWeek(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        Map<String, Long> ordersByDay = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            ordersByDay.put(day.getDisplayName(TextStyle.FULL, new Locale("ru")), 0L);
        }

        for (Object[] result : results) {
            Integer dayNum = ((Number) result[0]).intValue();
            Long count = ((Number) result[1]).longValue();
            DayOfWeek day = DayOfWeek.of(dayNum);
            ordersByDay.put(day.getDisplayName(TextStyle.FULL, new Locale("ru")), count);
        }

        return ordersByDay;
    }

    /**
     * Заказы по месяцам
     */
    private Map<String, Long> getOrdersByMonth(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepo.findOrdersByMonth(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        Map<String, Long> ordersByMonth = new LinkedHashMap<>();

        for (Object[] result : results) {
            String monthYear = result[0].toString();
            Long count = ((Number) result[1]).longValue();
            ordersByMonth.put(monthYear, count);
        }

        return ordersByMonth;
    }
}