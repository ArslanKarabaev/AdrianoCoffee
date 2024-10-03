package com.example.AdrianoCoffee.Config.SecurityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static com.example.AdrianoCoffee.Enum.Role.ADMIN;
import static com.example.AdrianoCoffee.Enum.Role.MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAutoFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {  // Встроенная поддержка CORS
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOriginPattern("*"); // Разрешаем все источники
            config.addAllowedHeader("*"); // Разрешаем любые заголовки
            config.addAllowedMethod("*"); // Разрешаем любые методы

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config); // Применяем ко всем путям

            cors.configurationSource(source);
        }).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/v2/auth/**",
                                "/v3/api-docs",
                                "/v2/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-recourses",
                                "/swagger-recourses/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers("api/v2/AdrianoCoffee/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                        .requestMatchers("api/v2/AdrianoCoffee/management/**").hasAnyAuthority(ADMIN.name(), MANAGER.name())

                        .requestMatchers("api/v2/AdrianoCoffee/admin/**").hasAnyRole(ADMIN.name())
                        .requestMatchers("api/v2/AdrianoCoffee/admin/**").hasAuthority(ADMIN.name())


                        .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAutoFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
