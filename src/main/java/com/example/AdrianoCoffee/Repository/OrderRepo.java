package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.Order;
import com.example.AdrianoCoffee.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    // Найти все заказы пользователя (используем @Query из-за user_id)
    @Query("SELECT o FROM Order o WHERE o.user.user_id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // Найти заказы по статусу
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    // Найти все заказы, отсортированные по дате
    List<Order> findAllByOrderByCreatedAtDesc();

    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    // ===== МЕТОДЫ ДЛЯ СТАТИСТИКИ =====

    /**
     * Подсчёт заказов за период
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    Long countOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Сумма выручки за период
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    Double sumRevenueBetweenDates(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Количество уникальных пользователей
     */
    @Query("SELECT COUNT(DISTINCT o.user.user_id) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    Long countDistinctUsersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Топ N блюд по количеству заказов
     */
    @Query(value = """
            SELECT 
                m.id,
                m.name,
                m.category,
                m.image_url,
                COUNT(DISTINCT oi.order_id) as times_ordered,
                SUM(oi.quantity) as total_quantity,
                SUM(oi.subtotal) as total_revenue,
                AVG(oi.menu_item_price) as average_price
            FROM order_items oi
            JOIN menus m ON oi.menu_item_id = m.id
            JOIN orders o ON oi.order_id = o.id
            WHERE o.created_at BETWEEN :startDate AND :endDate
            AND o.status != 'CANCELLED'
            GROUP BY m.id, m.name, m.category, m.image_url
            ORDER BY total_quantity DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopDishesRaw(@Param("limit") int limit,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Самое прибыльное блюдо
     */
    @Query(value = """
            SELECT 
                m.id,
                m.name,
                m.category,
                m.image_url,
                COUNT(DISTINCT oi.order_id) as times_ordered,
                SUM(oi.quantity) as total_quantity,
                SUM(oi.subtotal) as total_revenue,
                AVG(oi.menu_item_price) as average_price
            FROM order_items oi
            JOIN menus m ON oi.menu_item_id = m.id
            JOIN orders o ON oi.order_id = o.id
            WHERE o.created_at BETWEEN :startDate AND :endDate
            AND o.status != 'CANCELLED'
            GROUP BY m.id, m.name, m.category, m.image_url
            ORDER BY total_revenue DESC
            LIMIT 1
            """, nativeQuery = true)
    List<Object[]> findMostProfitableDishRaw(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Статистика по категориям
     */
    @Query(value = """
            SELECT 
                m.category,
                COUNT(DISTINCT oi.order_id) as orders_count,
                SUM(oi.quantity) as total_quantity,
                SUM(oi.subtotal) as total_revenue,
                AVG(oi.subtotal) as average_check
            FROM order_items oi
            JOIN menus m ON oi.menu_item_id = m.id
            JOIN orders o ON oi.order_id = o.id
            WHERE o.created_at BETWEEN :startDate AND :endDate
            AND o.status != 'CANCELLED'
            GROUP BY m.category
            ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<Object[]> findCategoryStatisticsRaw(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Заказы по часам
     */
    @Query(value = """
            SELECT 
                EXTRACT(HOUR FROM created_at) as hour,
                COUNT(*) as count
            FROM orders
            WHERE created_at BETWEEN :startDate AND :endDate
            AND status != 'CANCELLED'
            GROUP BY EXTRACT(HOUR FROM created_at)
            ORDER BY hour
            """, nativeQuery = true)
    List<Object[]> findOrdersByHour(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Заказы по дням недели (1=Понедельник, 7=Воскресенье)
     */
    @Query(value = """
            SELECT 
                EXTRACT(ISODOW FROM created_at) as dayOfWeek,
                COUNT(*) as count
            FROM orders
            WHERE created_at BETWEEN :startDate AND :endDate
            AND status != 'CANCELLED'
            GROUP BY EXTRACT(ISODOW FROM created_at)
            ORDER BY dayOfWeek
            """, nativeQuery = true)
    List<Object[]> findOrdersByDayOfWeek(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Заказы по месяцам
     */
    @Query(value = """
            SELECT 
                TO_CHAR(created_at, 'YYYY-MM') as monthYear,
                COUNT(*) as count
            FROM orders
            WHERE created_at BETWEEN :startDate AND :endDate
            AND status != 'CANCELLED'
            GROUP BY TO_CHAR(created_at, 'YYYY-MM')
            ORDER BY monthYear
            """, nativeQuery = true)
    List<Object[]> findOrdersByMonth(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query(value = """
    SELECT COALESCE(SUM(points_used), 0)
    FROM orders
    WHERE created_at BETWEEN :start AND :end
    AND status != 'CANCELLED'
    """, nativeQuery = true)
    Long sumPointsUsedBetweenDates(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM orders
    WHERE created_at BETWEEN :start AND :end
    AND status != 'CANCELLED'
    AND points_used > 0
    """, nativeQuery = true)
    Long countOrdersWithBonusBetweenDates(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}