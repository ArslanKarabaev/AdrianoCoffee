package com.example.AdrianoCoffee.Config.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Разрешаем все пути
                        .allowedOrigins("http://localhost:8080","http://localhost:5501", "https://adriano-coffee-production.up.railway.app/swagger-ui/index.html#/")  // Разрешаем запросы с определенных доменов
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Разрешаем определенные методы
                        .allowedHeaders("*")  // Разрешаем любые заголовки
                        .allowCredentials(true);  // Разрешаем учетные данные (куки)
            }
        };
    }

}
