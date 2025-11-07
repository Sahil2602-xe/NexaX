package com.project.demo.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsGlobalConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration cfg = new CorsConfiguration();

        cfg.setAllowedOrigins(Arrays.asList(
            "https://nexa-x-frontend.vercel.app",
            "https://nexa-x-frontend-shaikhsahil2602.vercel.app",
            "https://nexa-x-frontend-9xg5mnavb-shaikhsahil2602-9911s-projects.vercel.app",
            "http://localhost:5173"
        ));

        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(Collections.singletonList("*"));
        cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

        return new CorsFilter(source);
    }
}
