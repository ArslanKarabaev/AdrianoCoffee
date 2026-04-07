package com.example.AdrianoCoffee.Config.SecurityConfig;

import com.example.AdrianoCoffee.Service.JwtService.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем путь запроса
        String requestPath = request.getServletPath();

        // Пропускаем публичные эндпоинты БЕЗ проверки JWT токена
        if (requestPath.contains("/api/v2/auth") ||           // Регистрация/логин
                requestPath.contains("/api/v2/AdrianoCoffee/Menu") || // Меню для всех
                requestPath.startsWith("/images") ||           // ← ДОБАВЬТЕ
                requestPath.startsWith("/uploads") ||
                requestPath.contains("/swagger-ui") ||            // Swagger
                requestPath.contains("/v3/api-docs") ||
                requestPath.contains("/v2/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем заголовок Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Если нет заголовка или неправильный формат
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем JWT токен (убираем "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // Извлекаем email из токена
            userEmail = jwtService.extractUsername(jwt);

            // Если email есть и пользователь ещё не аутентифицирован
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Загружаем данные пользователя из БД
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Проверяем валидность токена
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // Создаём объект аутентификации
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Добавляем детали запроса
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Устанавливаем аутентификацию в контекст Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Логируем ошибку, но не блокируем запрос
            System.err.println("JWT Authentication error: " + e.getMessage());
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}