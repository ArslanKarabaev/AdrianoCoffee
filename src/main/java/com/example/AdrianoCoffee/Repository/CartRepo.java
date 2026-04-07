package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.Cart;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(Users user);

    @Query("SELECT c FROM Cart c WHERE c.user.user_id = :userId")
    List<Cart> findByUserId(@Param("userId") Long userId);

    Optional<Cart> findByUserAndMenuItem(Users user, Menu menuItem);

    void deleteByUser(Users user);

    @Modifying  // ← ВАЖНО для DELETE query!
    @Query("DELETE FROM Cart c WHERE c.user.user_id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}