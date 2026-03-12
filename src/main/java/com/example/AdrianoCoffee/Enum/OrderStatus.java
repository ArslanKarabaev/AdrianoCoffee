package com.example.AdrianoCoffee.Enum;

public enum OrderStatus {
    PENDING,      // Ожидает подтверждения
    CONFIRMED,    // Подтверждён
    PREPARING,    // Готовится
    READY,        // Готов к выдаче
    DELIVERED,    // Доставлен
    CANCELLED     // Отменён
}