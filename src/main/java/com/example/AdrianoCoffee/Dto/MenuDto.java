package com.example.AdrianoCoffee.Dto;

import com.example.AdrianoCoffee.Enum.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto    {
    private Long menu_id;
    private String name;
    private Integer price;
    private String description;
    private Category category;

    public MenuDto(String name, Integer price, String description, Category category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
    }
}
