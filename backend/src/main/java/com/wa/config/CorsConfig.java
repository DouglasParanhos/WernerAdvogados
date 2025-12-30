package com.wa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        // Permitir múltiplas origens usando allowedOriginPatterns quando credentials é true
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "http://localhost:5000"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setExposedHeaders(Arrays.asList("Content-Disposition", "Content-Type", "Content-Length", "Authorization"));
        config.setMaxAge(3600L); // Cache preflight por 1 hora
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

