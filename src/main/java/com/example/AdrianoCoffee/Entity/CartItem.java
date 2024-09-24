package com.example.AdrianoCoffee.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private Long item_id;
    @ManyToOne
    private Menu product;
    private int quantity;


}
