package com.wa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:5000}")
    private String allowedOrigins;
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        // Permitir múltiplas origens usando allowedOriginPatterns quando credentials é true
        // Lê da variável de ambiente CORS_ALLOWED_ORIGINS (separada por vírgula)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOriginPatterns(origins);
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setExposedHeaders(Arrays.asList("Content-Disposition", "Content-Type", "Content-Length", "Authorization"));
        config.setMaxAge(3600L); // Cache preflight por 1 hora
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

