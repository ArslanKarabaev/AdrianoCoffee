package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m WHERE m.name = ?1")
    Optional<Menu> findMenuByName(String name);

    @Query("SELECT m FROM Menu m WHERE m.menu_id = ?1")
    Optional<Menu> findMenuByMenu_id(Long id);

}
