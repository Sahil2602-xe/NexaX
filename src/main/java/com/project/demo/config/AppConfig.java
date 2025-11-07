package com.project.demo.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ✅ Public endpoints (auth & password reset)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/users/reset-password/**").permitAll()
                .requestMatchers("/api/users/reset-pass/**").permitAll()
                .requestMatchers("/api/users/verification/**").permitAll()
                // ✅ Secure everything else
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            // ✅ Allow both your deployed & preview URLs + localhost
            cfg.setAllowedOriginPatterns(Arrays.asList(
                "https://nexa-x-frontend.vercel.app",
                "https://*.vercel.app",
                "http://localhost:5173"
            ));

            cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
            cfg.setAllowedHeaders(Collections.singletonList("*"));
            cfg.setAllowCredentials(true);
            cfg.setExposedHeaders(Arrays.asList("Authorization"));
            cfg.setMaxAge(3600L);

            return cfg;
        };
    }
}
