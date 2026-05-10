package com.example.AdrianoCoffee.Dto;

import com.example.AdrianoCoffee.Enum.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private Long id;

    private String name;

    private Double price;

    private String description; // Здесь будем хранить "Состав: ..."

    private String volume; // Новое поле для "230 мл" или "310 гр"

    private String imageUrl; // Новое поле для пути к картинке (например, "mocha.jpeg")

    @Enumerated(EnumType.STRING) // Важно, чтобы в БД сохранялось слово (COFFEE), а не число (0)
    private Category category;

    private String nameEn;
    private String nameKg;
    private String descriptionEn;
    private String descriptionKg;
}
