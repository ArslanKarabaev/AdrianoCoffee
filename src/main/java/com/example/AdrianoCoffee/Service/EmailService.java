package com.example.AdrianoCoffee.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // ───── Базовый метод отправки ─────────────────────────
    private void sendEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("Email отправлен: " + toEmail);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("Ошибка отправки email: " + e.getMessage());
        }
    }

    // ───── Уведомление о статусе заказа ──────────────────
    public void sendOrderStatusEmail(String toEmail, String customerName,
                                     Long orderId, String status, Double totalPrice) {
        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("orderId", orderId);
        context.setVariable("totalPrice", String.format("%.0f", totalPrice));
        context.setVariable("statusText", getStatusText(status));
        context.setVariable("statusColor", getStatusColor(status));
        context.setVariable("statusEmoji", getStatusEmoji(status));
        context.setVariable("statusMessage", getStatusMessage(status));

        String html = templateEngine.process("email/order-status", context);
        String subject = "Adriano Coffee — Заказ #" + orderId + ": " + getStatusText(status);

        sendEmail(toEmail, subject, html);
    }

    // ───── Сброс пароля ───────────────────────────────────
    public void sendPasswordResetEmail(String toEmail, String customerName,
                                       String resetCode) {
        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("resetCode", resetCode);

        String html = templateEngine.process("email/password-reset", context);

        sendEmail(toEmail, "Adriano Coffee — Сброс пароля", html);
    }

    // ───── Вспомогательные методы ─────────────────────────
    private String getStatusText(String status) {
        return switch (status) {
            case "PENDING"   -> "Ожидает подтверждения";
            case "CONFIRMED" -> "Заказ подтверждён";
            case "PREPARING" -> "Готовится";
            case "READY"     -> "Готов к выдаче";
            case "DELIVERED" -> "Доставлен";
            case "CANCELLED" -> "Отменён";
            default          -> status;
        };
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "PENDING"   -> "#f39c12";
            case "CONFIRMED" -> "#2980b9";
            case "PREPARING" -> "#8e44ad";
            case "READY"     -> "#27ae60";
            case "DELIVERED" -> "#2ecc71";
            case "CANCELLED" -> "#e74c3c";
            default          -> "#95a5a6";
        };
    }

    private String getStatusEmoji(String status) {
        return switch (status) {
            case "PENDING"   -> "⏳";
            case "CONFIRMED" -> "✅";
            case "PREPARING" -> "👨‍🍳";
            case "READY"     -> "🔔";
            case "DELIVERED" -> "🎉";
            case "CANCELLED" -> "❌";
            default          -> "📦";
        };
    }

    private String getStatusMessage(String status) {
        return switch (status) {
            case "PENDING"   -> "Мы получили ваш заказ и скоро его подтвердим.";
            case "CONFIRMED" -> "Ваш заказ подтверждён и передан на кухню.";
            case "PREPARING" -> "Наши повара уже готовят ваш заказ!";
            case "READY"     -> "Ваш заказ готов! Ожидайте доставку.";
            case "DELIVERED" -> "Заказ доставлен. Приятного аппетита! ☕";
            case "CANCELLED" -> "Ваш заказ был отменён. Если это ошибка — свяжитесь с нами.";
            default          -> "";
        };
    }
}