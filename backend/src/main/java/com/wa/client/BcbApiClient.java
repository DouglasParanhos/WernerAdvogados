package com.wa.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BcbApiClient {
    
    private final RestTemplate restTemplate;
    private static final String BCB_API_BASE_URL = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.{serie}/dados";
    private static final int IPCA_E_SERIE = 433; // IPCA-E
    private static final int SELIC_SERIE = 11; // Taxa SELIC
    
    private final Map<String, Map<String, BigDecimal>> ipcaCache = new HashMap<>();
    private final Map<String, Map<String, BigDecimal>> selicCache = new HashMap<>();
    
    public BcbApiClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Busca IPCA-E histórico do BCB
     * Formato de data esperado: dd/MM/yyyy
     */
    public Map<String, BigDecimal> getIpcaEHistorico(String dataInicio, String dataFim) {
        String cacheKey = dataInicio + "_" + dataFim;
        if (ipcaCache.containsKey(cacheKey)) {
            return ipcaCache.get(cacheKey);
        }
        
        try {
            String url = BCB_API_BASE_URL.replace("{serie}", String.valueOf(IPCA_E_SERIE))
                    + "?formato=json&dataInicial=" + dataInicio + "&dataFinal=" + dataFim;
            
            log.info("Buscando IPCA-E do BCB: {}", url);
            
            BcbResponse[] response = restTemplate.getForObject(url, BcbResponse[].class);
            
            if (response != null && response.length > 0) {
                Map<String, BigDecimal> result = Arrays.stream(response)
                        .collect(Collectors.toMap(
                                BcbResponse::getData,
                                r -> BigDecimal.valueOf(r.getValor()),
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        ));
                ipcaCache.put(cacheKey, result);
                log.info("IPCA-E recuperado: {} registros", result.size());
                return result;
            }
        } catch (RestClientException e) {
            log.error("Erro ao buscar IPCA-E do BCB: {}", e.getMessage(), e);
        }
        
        return Collections.emptyMap();
    }
    
    /**
     * Busca Taxa SELIC histórica do BCB
     * Formato de data esperado: dd/MM/yyyy
     */
    public Map<String, BigDecimal> getSelicHistorico(String dataInicio, String dataFim) {
        String cacheKey = dataInicio + "_" + dataFim;
        if (selicCache.containsKey(cacheKey)) {
            log.info("SELIC: Retornando do cache para período {} a {}", dataInicio, dataFim);
            return selicCache.get(cacheKey);
        }
        
        try {
            String url = BCB_API_BASE_URL.replace("{serie}", String.valueOf(SELIC_SERIE))
                    + "?formato=json&dataInicial=" + dataInicio + "&dataFinal=" + dataFim;
            
            log.info("Buscando SELIC do BCB: {}", url);
            
            BcbResponse[] response = restTemplate.getForObject(url, BcbResponse[].class);
            
            if (response != null && response.length > 0) {
                Map<String, BigDecimal> result = Arrays.stream(response)
                        .collect(Collectors.toMap(
                                BcbResponse::getData,
                                r -> BigDecimal.valueOf(r.getValor()),
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        ));
                selicCache.put(cacheKey, result);
                log.info("SELIC recuperado: {} registros", result.size());
                if (!result.isEmpty()) {
                    String primeiraData = result.keySet().stream().min(String::compareTo).orElse("N/A");
                    String ultimaData = result.keySet().stream().max(String::compareTo).orElse("N/A");
                    BigDecimal primeiroValor = result.get(primeiraData);
                    log.info("SELIC: Primeira data: {}, Última data: {}, Primeiro valor: {}", 
                            primeiraData, ultimaData, primeiroValor);
                }
                return result;
            } else {
                log.warn("SELIC: Resposta vazia do BCB para período {} a {}", dataInicio, dataFim);
            }
        } catch (RestClientException e) {
            log.error("Erro ao buscar SELIC do BCB: {}", e.getMessage(), e);
        }
        
        return Collections.emptyMap();
    }
    
    /**
     * Busca IPCA-E para um mês específico (formato: MM/yyyy)
     */
    public BigDecimal getIpcaEPorMes(String mesAno) {
        // Converter MM/yyyy para período de busca (primeiro e último dia do mês)
        String[] parts = mesAno.split("/");
        if (parts.length != 2) {
            return BigDecimal.ZERO;
        }
        
        int mes = Integer.parseInt(parts[0]);
        int ano = Integer.parseInt(parts[1]);
        
        String dataInicio = String.format("01/%02d/%d", mes, ano);
        String dataFim = String.format("%02d/%02d/%d", 
                java.time.YearMonth.of(ano, mes).lengthOfMonth(), mes, ano);
        
        Map<String, BigDecimal> dados = getIpcaEHistorico(dataInicio, dataFim);
        
        // Retornar o primeiro valor encontrado no mês ou média dos valores
        Optional<BigDecimal> valor = dados.values().stream().findFirst();
        return valor.orElse(BigDecimal.ZERO);
    }
    
    /**
     * Busca SELIC para um mês específico (formato: MM/yyyy)
     */
    public BigDecimal getSelicPorMes(String mesAno) {
        String[] parts = mesAno.split("/");
        if (parts.length != 2) {
            return BigDecimal.ZERO;
        }
        
        int mes = Integer.parseInt(parts[0]);
        int ano = Integer.parseInt(parts[1]);
        
        String dataInicio = String.format("01/%02d/%d", mes, ano);
        String dataFim = String.format("%02d/%02d/%d", 
                java.time.YearMonth.of(ano, mes).lengthOfMonth(), mes, ano);
        
        Map<String, BigDecimal> dados = getSelicHistorico(dataInicio, dataFim);
        
        // Retornar média dos valores do mês (SELIC pode ter múltiplos valores no mês)
        OptionalDouble media = dados.values().stream()
                .mapToDouble(BigDecimal::doubleValue)
                .average();
        
        return media.isPresent() ? BigDecimal.valueOf(media.getAsDouble()) : BigDecimal.ZERO;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class BcbResponse {
        @JsonProperty("data")
        private String data;
        
        @JsonProperty("valor")
        private Double valor;
    }
}

