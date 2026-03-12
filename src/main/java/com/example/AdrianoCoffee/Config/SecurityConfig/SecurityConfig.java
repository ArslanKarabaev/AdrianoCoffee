package com.example.AdrianoCoffee.Config.SecurityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.AdrianoCoffee.Enum.Role.ADMIN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAutoFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
               // .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth

                     //   .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Разрешаем preflight-запросы
                        .requestMatchers(
                                "/**",
                                "/login-register.html",
                                "/login-register.css",
                                "/login-register.js",
                                "/register.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",

                                "/api/v2/auth/register",
                                "/api/v2/auth/authentication",
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

                        .requestMatchers("api/v2/AdrianoCoffee/admin/**").hasAnyRole(ADMIN.name())
                        .requestMatchers("api/v2/AdrianoCoffee/admin/**").hasAuthority(ADMIN.name())


                        .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAutoFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login-register.html")
                        .permitAll()
                );


        return http.build();
    }
}
