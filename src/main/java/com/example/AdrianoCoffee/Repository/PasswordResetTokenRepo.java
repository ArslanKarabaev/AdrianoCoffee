package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndCodeAndUsedFalse(String email, String code);
    void deleteByEmail(String email);
}
