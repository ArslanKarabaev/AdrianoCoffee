package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.StatisticsDto;
import com.example.AdrianoCoffee.Service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/AdrianoCoffee/Admin/statistics")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, maxAge = 3600)
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Получить полную статистику за период
     * GET /api/v2/AdrianoCoffee/Admin/statistics/dashboard?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            // По умолчанию - последние 30 дней
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            System.out.println("=== ЗАГРУЗКА СТАТИСТИКИ ===");
            System.out.println("Start date: " + startDate);
            System.out.println("End date: " + endDate);

            StatisticsDto stats = statisticsService.getGeneralStatistics(startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            response.put("period", Map.of(
                    "startDate", startDate,
                    "endDate", endDate
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // Выводим ошибку в консоль
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Ошибка при загрузке статистики: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Получить только общую статистику
     * GET /api/v2/AdrianoCoffee/Admin/statistics/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            StatisticsDto stats = statisticsService.getGeneralStatistics(startDate, endDate);

            Map<String, Object> overview = new HashMap<>();
            overview.put("totalOrders", stats.getTotalOrders());
            overview.put("totalRevenue", stats.getTotalRevenue());
            overview.put("averageCheck", stats.getAverageCheck());
            overview.put("activeUsers", stats.getActiveUsers());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", overview);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Получить топ блюд
     * GET /api/v2/AdrianoCoffee/Admin/statistics/top-dishes?limit=10
     */
    @GetMapping("/top-dishes")
    public ResponseEntity<Map<String, Object>> getTopDishes(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            StatisticsDto stats = statisticsService.getGeneralStatistics(startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("topDishes", stats.getTopDishes());
            response.put("mostPopular", stats.getMostPopular());
            response.put("mostProfitable", stats.getMostProfitable());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Получить статистику по категориям
     * GET /api/v2/AdrianoCoffee/Admin/statistics/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategoryStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            StatisticsDto stats = statisticsService.getGeneralStatistics(startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats.getCategoryStats());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Получить статистику по времени
     * GET /api/v2/AdrianoCoffee/Admin/statistics/time-analysis
     */
    @GetMapping("/time-analysis")
    public ResponseEntity<Map<String, Object>> getTimeAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            StatisticsDto stats = statisticsService.getGeneralStatistics(startDate, endDate);

            Map<String, Object> timeData = new HashMap<>();
            timeData.put("ordersByHour", stats.getOrdersByHour());
            timeData.put("ordersByDayOfWeek", stats.getOrdersByDayOfWeek());
            timeData.put("ordersByMonth", stats.getOrdersByMonth());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", timeData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}