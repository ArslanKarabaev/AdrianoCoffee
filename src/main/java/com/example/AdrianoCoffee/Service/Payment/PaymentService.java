package com.example.AdrianoCoffee.Service.Payment;

public interface PaymentService {

    /**
     * Создаёт платёжное намерение и возвращает clientSecret
     * который нужен фронтенду для отображения формы оплаты.
     *
     * @param orderId   ID заказа
     * @param amount    сумма в сомах
     * @param currency  валюта (например "kgs" или "usd")
     * @return clientSecret строка для фронтенда
     */
    String createPaymentIntent(Long orderId, Double amount, String currency) throws Exception;

    /**
     * Подтверждает оплату после webhook от платёжной системы.
     * Меняет статус заказа на CONFIRMED и paymentStatus на PAID.
     *
     * @param paymentIntentId ID платёжного намерения
     * @return
     */
    Long confirmPayment(String paymentIntentId) throws Exception;

    /**
     * Возврат платежа
     *
     * @param orderId ID заказа
     */
    void refundPayment(Long orderId) throws Exception;
}