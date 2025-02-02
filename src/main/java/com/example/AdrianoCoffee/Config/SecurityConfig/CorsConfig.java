package com.example.AdrianoCoffee.Config.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig{

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080","http://localhost:5500", "https://adrianocoffee-production.up.railway.app/swagger-ui/index.html#/")); // Разрешенные домены
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Разрешенные методы
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Разрешенные заголовки
        config.setExposedHeaders(List.of("Authorization")); // Заголовки, которые могут читаться в ответе
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
