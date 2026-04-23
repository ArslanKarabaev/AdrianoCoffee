package com.example.AdrianoCoffee.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bonus_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String type;    //  "EARNED","SPENT"

    @Column(nullable = false)
    private String source;  // "MANUAL" , "ORDER"

    private String description;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {createdAt = LocalDateTime.now(); }

}
