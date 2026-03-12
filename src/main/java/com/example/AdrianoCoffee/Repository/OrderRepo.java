package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.Order;
import com.example.AdrianoCoffee.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    // Найти все заказы пользователя (используем @Query из-за user_id)
    @Query("SELECT o FROM Order o WHERE o.user.user_id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // Найти заказы по статусу
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    // Найти все заказы, отсортированные по дате
    List<Order> findAllByOrderByCreatedAtDesc();
}