package com.example.AdrianoCoffee.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class OrderCart {
    @Id
    @GeneratedValue
    private Long order_id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> items;
    @Transient
    private Integer sum;

    public Integer getSum() {
        for (CartItem item: items) {
           sum = item.getProduct().getPrice() * item.getQuantity();
        }
        return sum;
    }

}
