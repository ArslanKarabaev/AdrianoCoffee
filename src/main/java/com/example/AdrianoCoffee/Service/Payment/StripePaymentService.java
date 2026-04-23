package com.example.AdrianoCoffee.Service.Payment;

import com.example.AdrianoCoffee.Entity.Order;
import com.example.AdrianoCoffee.Enum.OrderStatus;
import com.example.AdrianoCoffee.Repository.OrderRepo;
import com.example.AdrianoCoffee.Service.CartService;
import com.example.AdrianoCoffee.Service.OrderService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "payment.provider", havingValue = "stripe")
public class StripePaymentService implements PaymentService {

    private final OrderRepo orderRepo;
    private final CartService cartService;
    private final OrderService orderService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public StripePaymentService(OrderRepo orderRepo, CartService cartService,
                                @Lazy OrderService orderService) {
        this.orderRepo = orderRepo;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @PostConstruct
    public void init() { Stripe.apiKey = stripeSecretKey; }

    // Базовый метод интерфейса — не используем напрямую
    @Override
    public String createPaymentIntent(Long userId, Double amount, String currency) throws Exception {
        return createPaymentIntentWithDetails(userId, currency, null, null, null, 0);
    }

    // Создаёт PaymentIntent с деталями заказа в metadata
    public String createPaymentIntentWithDetails(Long userId, String currency,
                                                 String deliveryAddress, String phone,
                                                 String comment, int pointsToUse) throws Exception {
        Double cartTotal = cartService.calculateTotal(userId);
        if (cartTotal <= 0) throw new IllegalStateException("Корзина пуста");

        // Вычитаем баллы из суммы (1 балл = 1 сом)
        double discountedTotal = Math.max(0, cartTotal - pointsToUse);
        long amountInCents = Math.round(discountedTotal * 100);
        if (amountInCents < 50) amountInCents = 50; // минимум Stripe

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .putMetadata("userId", userId.toString())
                .putMetadata("deliveryAddress", deliveryAddress != null ? deliveryAddress : "")
                .putMetadata("phone", phone != null ? phone : "")
                .putMetadata("comment", comment != null ? comment : "")
                .putMetadata("pointsUsed", String.valueOf(pointsToUse))
                .setDescription("Заказ в Adriano Coffee")
                .build();

        return PaymentIntent.create(params).getClientSecret();
    }

    // Вызывается из webhook — создаёт заказ после оплаты
    @Override
    @Transactional
    public Long confirmPayment(String paymentIntentId) throws Exception {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        String userId = intent.getMetadata().get("userId");
        String deliveryAddress = intent.getMetadata().get("deliveryAddress");
        String phone = intent.getMetadata().get("phone");
        String comment = intent.getMetadata().get("comment");
        int pointsUsed = 0;
        try {
            pointsUsed = Integer.parseInt(intent.getMetadata().getOrDefault("pointsUsed", "0"));
        } catch (NumberFormatException ignored) {}

        if (userId == null) throw new IllegalStateException("userId не найден в metadata");

        var orderDto = orderService.createOrderAfterPayment(
                Long.parseLong(userId), deliveryAddress, phone, comment,
                paymentIntentId, pointsUsed
        );
        return orderDto.getId();
    }

    // Возврат при отмене заказа
    @Override
    @Transactional
    public void refundPayment(Long orderId) throws Exception {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Возврат только для заказов со статусом PAID");
        }
        if (order.getPaymentIntentId() == null) {
            throw new IllegalStateException("PaymentIntent не найден");
        }

        Refund.create(RefundCreateParams.builder()
                .setPaymentIntent(order.getPaymentIntentId())
                .build());
    }
}