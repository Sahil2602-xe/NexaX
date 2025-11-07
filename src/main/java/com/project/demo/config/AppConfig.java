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
            // ✅ Stateless JWT authentication
            .sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Public vs protected endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()                      // public auth routes
                .requestMatchers("/api/users/reset-password/**").permitAll()  // password reset
                .requestMatchers("/api/users/reset-pass/**").permitAll()
                .requestMatchers("/api/users/verification/**").permitAll()
                .requestMatchers("/coins/**").permitAll()                     // public coin APIs
                .anyRequest().authenticated()                                 // everything else protected
            )

            // ✅ Add JWT filter
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)

            // ✅ Disable CSRF
            .csrf(csrf -> csrf.disable())

            // ✅ Enable CORS for frontend → backend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            cfg.setAllowedOrigins(Arrays.asList(
                "https://nexa-x-frontend.vercel.app",
                "https://nexa-x-frontend-9xg5mnavb-shaikhsahil2602-9911s-projects.vercel.app",
                "http://localhost:5173"
            ));

            cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cfg.setAllowedHeaders(Collections.singletonList("*"));
            cfg.setAllowCredentials(true);
            cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
            cfg.setMaxAge(3600L);

            return cfg;
        };
    }
}
