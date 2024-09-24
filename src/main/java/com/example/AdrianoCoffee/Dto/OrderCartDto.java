package com.example.AdrianoCoffee.Dto;

import com.example.AdrianoCoffee.Entity.CartItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCartDto {
    private Long order_id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> items;
    private Integer sum;

    public OrderCartDto(List<CartItem> items, Integer sum) {
        this.items = items;
        this.sum = sum;
    }
}
