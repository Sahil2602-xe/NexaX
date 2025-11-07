package com.project.demo.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            // ✅ JWT-based auth should be stateless
            .sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                // ✅ Allow preflight (OPTIONS) requests globally
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/coins/**").permitAll()
                .requestMatchers("/api/users/reset-password/**").permitAll()
                .requestMatchers("/api/users/reset-pass/**").permitAll()
                .requestMatchers("/api/users/verification/**").permitAll()

                // ✅ Everything else requires authentication
                .anyRequest().authenticated()
            )

            // ✅ Add JWT filter
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)

            // ✅ Disable CSRF for APIs
            .csrf(csrf -> csrf.disable())

            // ✅ Enable global CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            // ✅ Allow all your frontend URLs
            cfg.setAllowedOrigins(Arrays.asList(
                "https://nexa-x-frontend.vercel.app",
                "https://nexa-x-frontend.vercel.app/",
                "https://nexa-x-frontend-shaikhsahil2602.vercel.app",
                "https://nexa-x-frontend-9xg5mnavb-shaikhsahil2602-9911s-projects.vercel.app",
                "http://localhost:5173"
            ));

            // ✅ Allow all required HTTP methods
            cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

            // ✅ Allow all headers (especially Authorization)
            cfg.setAllowedHeaders(Collections.singletonList("*"));

            // ✅ Allow credentials
            cfg.setAllowCredentials(true);

            // ✅ Expose Authorization header to frontend
            cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

            // Cache preflight responses for 1 hour
            cfg.setMaxAge(3600L);

            return cfg;
        };
    }
}
