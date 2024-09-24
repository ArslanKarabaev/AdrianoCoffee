package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.OrderCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderCartRepo extends JpaRepository<OrderCart, Long> {
    @Query("SELECT o FROM OrderCart o WHERE o.order_id = ?1")
    Optional<OrderCart> findOrderByOrderName(String name);
}
