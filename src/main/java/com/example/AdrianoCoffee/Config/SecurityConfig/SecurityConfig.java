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

import static com.example.AdrianoCoffee.Enum.Role.*;

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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

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

                        .requestMatchers("/api/v2/AdrianoCoffee/Menu/**").permitAll()

                        .requestMatchers("/api/v2/AdrianoCoffee/Admin/**").hasRole(ADMIN.name())

                        .requestMatchers("/api/v2/AdrianoCoffee/Management/**").hasAnyRole(MANAGER.name(), ADMIN.name())

                        .requestMatchers("/api/v2/AdrianoCoffee/User/**").hasAnyRole(USER.name(), ADMIN.name(), MANAGER.name())

                        .requestMatchers("/api/v2/Cart/**").authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAutoFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}