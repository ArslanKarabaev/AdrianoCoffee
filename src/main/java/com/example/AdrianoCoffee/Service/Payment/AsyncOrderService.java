package com.example.AdrianoCoffee.Service.Payment;

import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Service.BonusService;
import com.example.AdrianoCoffee.Service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncOrderService {

    private final StripePaymentService stripePaymentService;
    private final BonusService bonusService;
    private final TranslationService translationService;
    private final MenuRepo menuRepo;

    public CompletableFuture<Long> confirmPaymentAsync(String paymentIntentId) {
        try {
            Long orderId = stripePaymentService.confirmPayment(paymentIntentId);
            return CompletableFuture.completedFuture(orderId);
        } catch (Exception e) {
            log.error("Ошибка подтверждения оплаты: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    public void spendPointsAsync(Long userId, int points, Long orderId) {
        try {
            bonusService.spendPoints(userId, points, orderId);
        } catch (Exception e) {
            log.error("Ошибка списания баллов: {}", e.getMessage());
        }
    }

    // В AsyncOrderService.java добавь:
    @Async("taskExecutor")
    public void translateMenuAsync(Menu menu) {
        try {
            translationService.translateMenu(menu);
            menuRepo.save(menu); // сохраняем переводы после перевода
        } catch (Exception e) {
            log.error("Ошибка перевода блюда #{}: {}", menu.getId(), e.getMessage());
        }
    }
}