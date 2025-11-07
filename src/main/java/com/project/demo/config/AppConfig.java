package com.project.demo.config;

import java.util.Arrays;
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
                // allow authentication endpoints publicly
                .requestMatchers("/auth/**").permitAll()
                // allow these user endpoints for password reset and verification
                .requestMatchers("/api/users/reset-password/**", "/api/users/reset-pass/**", "/api/users/verification/**").permitAll()
                // admin routes require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // all other /api endpoints must be authenticated
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
            // allow the exact origins you use in production + local dev
            List<String> allowedOrigins = Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://nexax.up.railway.app",
                "https://nexa-x-frontend.vercel.app",
                "https://nexa-x-frontend-9xg5mnavb-shaikhsahil2602-9911s-projects.vercel.app" // any Vercel variant you used
            );

            cfg.setAllowedOrigins(allowedOrigins);
            cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
            cfg.setAllowedHeaders(Arrays.asList("*"));
            cfg.setAllowCredentials(true);
            cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
            cfg.setMaxAge(3600L);
            return cfg;
        };
    }
}
