package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.BonusTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BonusTransactionRepo extends JpaRepository<BonusTransaction, Long> {

    @Query("SELECT bt FROM BonusTransaction bt WHERE bt.user.user_id = :userId ORDER BY bt.createdAt DESC")
    List<BonusTransaction> findByUserId(@Param("userId") Long userId);
}
