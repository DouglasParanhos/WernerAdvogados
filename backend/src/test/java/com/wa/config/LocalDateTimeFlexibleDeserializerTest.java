package com.wa.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeFlexibleDeserializerTest {

    private ObjectMapper objectMapper;
    private JacksonConfig.LocalDateTimeFlexibleDeserializer deserializer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        deserializer = new JacksonConfig.LocalDateTimeFlexibleDeserializer();
    }
    
    // Helper method para criar JsonParser a partir de uma string e avançar para o token
    private JsonParser createJsonParser(String jsonValue) throws IOException {
        JsonParser parser = objectMapper.createParser("\"" + jsonValue + "\"");
        parser.nextToken(); // Avança para o token VALUE_STRING
        return parser;
    }

    @Test
    void testDeserializeBrazilianDateFormat() throws IOException {
        // Testa formato brasileiro: dd/MM/yyyy
        String dateString = "05/12/1946";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        assertEquals(1946, result.getYear());
        assertEquals(12, result.getMonthValue());
        assertEquals(5, result.getDayOfMonth());
        assertEquals(0, result.getHour());
        assertEquals(0, result.getMinute());
        assertEquals(0, result.getSecond());
    }

    @Test
    void testDeserializeIsoDateFormat() throws IOException {
        // Testa formato ISO: yyyy-MM-dd
        String dateString = "1946-12-05";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        assertEquals(1946, result.getYear());
        assertEquals(12, result.getMonthValue());
        assertEquals(5, result.getDayOfMonth());
        assertEquals(0, result.getHour());
        assertEquals(0, result.getMinute());
        assertEquals(0, result.getSecond());
    }

    @Test
    void testDeserializeDateTimeFormat() throws IOException {
        // Testa formato com data e hora: yyyy-MM-dd'T'HH:mm:ss
        String dateString = "1946-12-05T14:30:45";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        assertEquals(1946, result.getYear());
        assertEquals(12, result.getMonthValue());
        assertEquals(5, result.getDayOfMonth());
        assertEquals(14, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(45, result.getSecond());
    }

    @Test
    void testDeserializeDateTimeWithMillisFormat() throws IOException {
        // Testa formato com data, hora e milissegundos: yyyy-MM-dd'T'HH:mm:ss.SSS
        String dateString = "1946-12-05T14:30:45.123";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        assertEquals(1946, result.getYear());
        assertEquals(12, result.getMonthValue());
        assertEquals(5, result.getDayOfMonth());
        assertEquals(14, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(45, result.getSecond());
        assertEquals(123000000, result.getNano()); // 123 milissegundos = 123000000 nanossegundos
    }

    @Test
    void testDeserializeWithWhitespace() throws IOException {
        // Testa que espaços em branco são removidos
        String dateString = "  1946-12-05  ";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        assertEquals(1946, result.getYear());
        assertEquals(12, result.getMonthValue());
        assertEquals(5, result.getDayOfMonth());
    }

    @Test
    void testDeserializeInvalidFormat() throws IOException {
        // Testa que formato inválido lança exceção
        String dateString = "invalid-date-format";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        IOException exception = assertThrows(IOException.class, () -> {
            deserializer.deserialize(parser, context);
        });
        
        assertTrue(exception.getMessage().contains("Não foi possível deserializar data"));
    }

    @Test
    void testDeserializeDifferentBrazilianDates() throws IOException {
        // Testa diferentes datas no formato brasileiro
        String[] dates = {
            "01/01/2000",
            "31/12/1999",
            "15/06/2023",
            "29/02/2024" // Ano bissexto
        };
        
        for (String dateString : dates) {
            JsonParser parser = createJsonParser(dateString);
            DeserializationContext context = objectMapper.getDeserializationContext();
            
            LocalDateTime result = deserializer.deserialize(parser, context);
            
            assertNotNull(result, "Data não deveria ser null: " + dateString);
            assertEquals(0, result.getHour(), "Hora deveria ser 00: " + dateString);
            assertEquals(0, result.getMinute(), "Minuto deveria ser 00: " + dateString);
        }
    }

    @Test
    void testDeserializeDifferentIsoDates() throws IOException {
        // Testa diferentes datas no formato ISO
        String[] dates = {
            "2000-01-01",
            "1999-12-31",
            "2023-06-15",
            "2024-02-29" // Ano bissexto
        };
        
        for (String dateString : dates) {
            JsonParser parser = createJsonParser(dateString);
            DeserializationContext context = objectMapper.getDeserializationContext();
            
            LocalDateTime result = deserializer.deserialize(parser, context);
            
            assertNotNull(result, "Data não deveria ser null: " + dateString);
            assertEquals(0, result.getHour(), "Hora deveria ser 00: " + dateString);
            assertEquals(0, result.getMinute(), "Minuto deveria ser 00: " + dateString);
        }
    }

    @Test
    void testDeserializeMidnightConversion() throws IOException {
        // Testa que datas sem hora são convertidas para meia-noite
        String dateString = "1946-12-05";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        LocalDateTime expected = LocalDateTime.of(1946, 12, 5, 0, 0, 0);
        assertEquals(expected, result);
    }

    @Test
    void testDeserializeBrazilianDateWithTime() throws IOException {
        // Testa que formato brasileiro sem hora usa meia-noite
        String dateString = "05/12/1946";
        JsonParser parser = createJsonParser(dateString);
        DeserializationContext context = objectMapper.getDeserializationContext();
        
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        assertNotNull(result);
        LocalDateTime expected = LocalDateTime.of(1946, 12, 5, 0, 0, 0);
        assertEquals(expected, result);
    }
}

