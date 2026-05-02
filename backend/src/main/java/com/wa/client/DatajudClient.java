package com.wa.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wa.config.DatajudProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP client for CNJ DataJud TJRJ API — mirrors consultaprocessual/scripts/datajud_client.py.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatajudClient {

    private final DatajudProperties properties;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public Map<String, Object> search(Map<String, Object> payload) {
        String apiKey = properties.getKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("DATAJUD_API_KEY não configurada no servidor.");
        }

        RestTemplate restTemplate = createRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "APIKey " + apiKey);

        String body;
        try {
            body = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar payload DataJud", e);
        }

        int maxAttempts = properties.getMaxRetries() + 1;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        properties.getUrl(),
                        HttpMethod.POST,
                        entity,
                        String.class
                );
                String responseBody = response.getBody();
                if (responseBody == null || responseBody.isBlank()) {
                    return Map.of();
                }
                try {
                    return objectMapper.readValue(responseBody, Map.class);
                } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
                    throw new RuntimeException("Resposta DataJud inválida (JSON)", jpe);
                }
            } catch (HttpStatusCodeException exc) {
                int code = exc.getStatusCode().value();
                boolean retryable = code == 408 || code == 429 || code >= 500;
                if (!retryable || attempt >= properties.getMaxRetries()) {
                    String details = truncate(exc.getResponseBodyAsString(), 500);
                    throw new RuntimeException(
                            "Erro HTTP " + code + " ao consultar Datajud: " + details,
                            exc
                    );
                }
                log.warn("DataJud HTTP {} — tentativa {}/{}, repetindo...", code, attempt + 1, maxAttempts);
            } catch (RestClientException exc) {
                if (attempt >= properties.getMaxRetries()) {
                    throw new RuntimeException("Falha de rede ao consultar Datajud: " + exc.getMessage(), exc);
                }
                log.warn("DataJud rede — tentativa {}: {}", attempt + 1, exc.getMessage());
            }

            sleepBackoff(attempt);
        }

        throw new RuntimeException("Não foi possível consultar Datajud após retentativas.");
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeoutMs());
        factory.setReadTimeout(properties.getTimeoutMs());
        return new RestTemplate(factory);
    }

    private void sleepBackoff(int attempt) {
        double seconds = properties.getBackoffBaseSeconds() * Math.pow(2, attempt);
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrompido durante backoff DataJud", e);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "sem detalhes no corpo da resposta";
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
