package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Entity.BonusTransaction;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.BonusTransactionRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BonusService {
    private static final double EARN_RATE = 0.05;

    private final UsersRepo usersRepo;
    private final BonusTransactionRepo bonusTransactionRepo;

    @Transactional
    public void earnPointsForOrder(Long userId, Double orderTotal, Long orderId) {
        Users user = getUser(userId);
        int points = (int) Math.floor(orderTotal * EARN_RATE);
        if (points <= 0) return;

        user.setBonusPoints(user.getBonusPoints() + points);
        usersRepo.save(user);

        bonusTransactionRepo.save(BonusTransaction.builder()
                .user(user)
                .points(points)
                .type("EARNED")
                .source("ORDER")
                .description("Начисленно за заказ # " + orderId)
                .orderId(orderId)
                .build());
    }

    @Transactional
    public void spendPoints(Long userId, int points, Long orderId) {
        Users user = getUser(userId);
        if (user.getBonusPoints() < points) {
            throw new IllegalStateException("У вас недостаточно баллов");
        }

        user.setBonusPoints(user.getBonusPoints() - points);
        usersRepo.save(user);

        bonusTransactionRepo.save(BonusTransaction.builder()
                .user(user)
                .points(points)
                .type("SPENT")
                .source("ORDER")
                .description("Списано при оплате заказа # " + orderId)
                .orderId(orderId)
                .build());
    }

    @Transactional
    public void earnPointsManual(Long userId, Double purchaseAmount, String description) {
        Users user = getUser(userId);
        int points = (int) Math.floor(purchaseAmount * EARN_RATE);
        if (points <= 0) return;

        user.setBonusPoints(user.getBonusPoints() + points);
        usersRepo.save(user);

        bonusTransactionRepo.save(BonusTransaction.builder()
                .user(user)
                .points(points)
                .type("EARNED")
                .source("MANUAL")
                .description(description != null ? description : "Начислено за покупку в филиале")
                .build());
    }

    public int getBalance(Long userId) {
        return getUser(userId).getBonusPoints();
    }

    public List<BonusTransaction> getHistory(Long userId) {
        return bonusTransactionRepo.findByUserId(userId);
    }

    private Users getUser(Long userId) {
        return usersRepo.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}
