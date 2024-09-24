package com.example.AdrianoCoffee.Entity;

import com.example.AdrianoCoffee.Enum.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Menu {

    @Id
    @GeneratedValue
    private Long menu_id;
    private String name;
    private Integer price;
    private String description;
    private Category category;
    // дневник надо заполнить
}
