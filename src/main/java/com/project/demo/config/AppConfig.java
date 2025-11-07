// src/main/java/com/project/demo/config/AppConfig.java
package com.project.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // public endpoints
                .requestMatchers("/auth/**", "/coins/**", "/public/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // forgot/reset endpoints you had
                .requestMatchers("/api/users/reset-password/**", "/api/users/reset-pass/**", "/api/users/verification/**").permitAll()
                // admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // any other api requires authentication
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest request) -> {
            CorsConfiguration cfg = new CorsConfiguration();
            // add your frontend origin(s) here
            cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://nexa-x-frontend.vercel.app",
                "https://nexa-x-frontend-9xg5mnavb-shaikhsahil2602-9911s-projects.vercel.app", // if used
                "https://nexax.up.railway.app"
            ));
            cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
            cfg.setAllowedHeaders(List.of("*"));
            cfg.setAllowCredentials(true);
            cfg.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
            cfg.setMaxAge(3600L);
            return cfg;
        };
    }
}
