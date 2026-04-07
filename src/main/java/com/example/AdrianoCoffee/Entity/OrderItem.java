package com.example.AdrianoCoffee.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private Menu menuItem;

    @Column(name = "menu_item_name", nullable = false)
    private String menuItemName;

    @Column(name = "menu_item_price", nullable = false)
    private Double menuItemPrice;

    @Column(name = "menu_item_image")
    private String menuItemImage;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double subtotal;
}