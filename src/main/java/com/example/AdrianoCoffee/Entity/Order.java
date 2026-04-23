package com.example.AdrianoCoffee.Entity;

import com.example.AdrianoCoffee.Enum.OrderStatus;
import com.example.AdrianoCoffee.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ───── Поля для оплаты ─────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    // ID платёжного намерения от Stripe (или другого провайдера)
    @Column(name = "payment_intent_id", length = 255)
    private String paymentIntentId;

    @Column(name = "payment_method")
    private String paymentMethod; // "card", "cash"

    @Column(name = "points_used")
    @Builder.Default
    private Integer pointsUsed = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}