package com.example.AdrianoCoffee.Dto;

import com.example.AdrianoCoffee.Enum.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuDto {
    private Long id;

    private String name;

    private Double price;

    private String description; // Здесь будем хранить "Состав: ..."

    private String volume; // Новое поле для "230 мл" или "310 гр"

    private String imageUrl; // Новое поле для пути к картинке (например, "mocha.jpeg")

    @Enumerated(EnumType.STRING) // Важно, чтобы в БД сохранялось слово (COFFEE), а не число (0)
    private Category category;

    public MenuDto(Long id, String name, Double price, String description, String volume, String imageUrl, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.volume = volume;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
