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
            // ✅ Make session stateless for JWT
            .sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Main authorization rules
            .authorizeHttpRequests(auth -> auth
                // --- Public endpoints (no JWT needed) ---
                .requestMatchers(
                    "/auth/**",
                    "/api/users/reset-password/**",
                    "/api/users/reset-pass/**",
                    "/api/users/verification/**",
                    "/api/public/**"
                ).permitAll()

                // --- Admin-only access ---
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // --- Authenticated endpoints (JWT required) ---
                .requestMatchers(
                    "/api/users/**",
                    "/api/wallet/**",
                    "/api/transactions/**",
                    "/api/2fa/**"
                ).authenticated()

                // --- Everything else allowed ---
                .anyRequest().permitAll()
            )

            // ✅ JWT filter
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)

            // ✅ Disable CSRF for API
            .csrf(csrf -> csrf.disable())

            // ✅ Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            // ✅ Allow your local + production frontend
            cfg.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://nexa-x-frontend.vercel.app"
            ));

            cfg.setAllowedMethods(Collections.singletonList("*"));
            cfg.setAllowedHeaders(Collections.singletonList("*"));
            cfg.setAllowCredentials(true);
            cfg.setExposedHeaders(Arrays.asList("Authorization"));
            cfg.setMaxAge(3600L);

            return cfg;
        };
    }
}
