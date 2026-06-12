package com.example.AdrianoCoffee.Entity;

import com.example.AdrianoCoffee.Enum.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "menus") // Хорошая практика давать имя таблице во множественном числе
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Рекомендуется для автоинкремента
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
