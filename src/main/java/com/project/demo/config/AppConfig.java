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
            // ✅ Use stateless session for JWT-based auth
            .sessionManagement(management -> 
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Define which endpoints are open and which require auth
            .authorizeHttpRequests(auth -> auth
    // Public endpoints
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/api/users/reset-password/**").permitAll()
    .requestMatchers("/api/users/reset-pass/**").permitAll()
    .requestMatchers("/api/users/verification/**").permitAll()

    // Make coins endpoints public (allows frontend to call /coins and /coins/details/:id without auth)
    .requestMatchers("/coins/**").permitAll()

    // Everything else requires auth
    .anyRequest().authenticated()
)


            // ✅ Add your JWT validator before authentication happens
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)

            // ✅ Disable CSRF for API
            .csrf(csrf -> csrf.disable())

            // ✅ Enable CORS for frontend → backend calls
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            // ✅ Allow your frontend and local dev origins
            cfg.setAllowedOrigins(Arrays.asList(
                "https://nexa-x-frontend.vercel.app",
                "https://nexa-x-frontend-9xg5mnavb-shaikhsahil2602-9911s-projects.vercel.app",
                "http://localhost:5173"
            ));

            // ✅ Allow common HTTP methods
            cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

            // ✅ Allow all headers and credentials
            cfg.setAllowedHeaders(Collections.singletonList("*"));
            cfg.setAllowCredentials(true);

            // ✅ Expose Authorization header to frontend
            cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

            // Cache preflight response
            cfg.setMaxAge(3600L);
            return cfg;
        };
    }
}
