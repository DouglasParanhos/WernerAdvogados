package com.wa.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        // Cria o ObjectMapper com o builder
        ObjectMapper objectMapper = builder.build();
        
        // Cria um módulo simples com nosso deserializador customizado
        SimpleModule customModule = new SimpleModule("LocalDateTimeFlexibleModule");
        customModule.addDeserializer(LocalDateTime.class, new LocalDateTimeFlexibleDeserializer());
        
        // Registra nosso módulo customizado
        objectMapper.registerModule(customModule);
        
        // Também registra o JavaTimeModule para serialização (mas nosso deserializador tem prioridade)
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
    
    
    /**
     * Deserializador flexível que aceita:
     * - "dd/MM/yyyy" (formato brasileiro, apenas data, converte para meia-noite)
     * - "yyyy-MM-dd" (formato ISO, apenas data, converte para meia-noite)
     * - "yyyy-MM-dd'T'HH:mm:ss" (data e hora)
     * - "yyyy-MM-dd'T'HH:mm:ss.SSS" (data, hora e milissegundos)
     */
    static class LocalDateTimeFlexibleDeserializer extends StdDeserializer<LocalDateTime> {
        
        private static final DateTimeFormatter DATE_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final DateTimeFormatter DATE_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        private static final DateTimeFormatter DATE_TIME_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        
        public LocalDateTimeFlexibleDeserializer() {
            super(LocalDateTime.class);
        }
        
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText().trim();
            
            // Tenta primeiro com data e hora completa
            try {
                if (dateString.contains("T") && dateString.contains(".")) {
                    return LocalDateTime.parse(dateString, DATE_TIME_MILLIS);
                } else if (dateString.contains("T")) {
                    return LocalDateTime.parse(dateString, DATE_TIME);
                }
            } catch (DateTimeParseException e) {
                // Continua para tentar apenas data
            }
            
            // Tenta formato brasileiro (dd/MM/yyyy)
            try {
                LocalDate date = LocalDate.parse(dateString, DATE_BR);
                return date.atStartOfDay();
            } catch (DateTimeParseException e) {
                // Continua para tentar formato ISO
            }
            
            // Tenta formato ISO (yyyy-MM-dd)
            try {
                LocalDate date = LocalDate.parse(dateString, DATE_ISO);
                return date.atStartOfDay();
            } catch (DateTimeParseException e) {
                throw new IOException("Não foi possível deserializar data: " + dateString + 
                    ". Formatos aceitos: dd/MM/yyyy, yyyy-MM-dd, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSS", e);
            }
        }
    }
}

