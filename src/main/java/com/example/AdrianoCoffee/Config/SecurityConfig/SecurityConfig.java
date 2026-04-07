package com.example.AdrianoCoffee.Config.SecurityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.AdrianoCoffee.Enum.Role.ADMIN;
import static com.example.AdrianoCoffee.Enum.Role.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAutoFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем OPTIONS запросы (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Публичные эндпоинты (регистрация, логин)
                        .requestMatchers(
                                "/api/v2/auth/register",
                                "/api/v2/auth/authentication",
                                "/v2/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // Меню доступно всем (гостям тоже)
                        .requestMatchers("/api/v2/AdrianoCoffee/Menu/**").permitAll()

                        // Только для администраторов (с БОЛЬШОЙ буквы Admin!)
                        .requestMatchers("/api/v2/AdrianoCoffee/Admin/**").hasRole(ADMIN.name())

                        // Только для авторизованных пользователей
                        .requestMatchers("/api/v2/AdrianoCoffee/User/**").hasAnyRole(USER.name(), ADMIN.name())

                        // Корзина только для авторизованных
                        .requestMatchers("/api/v2/Cart/**").authenticated()

                        // Все остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAutoFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}