package com.wa.controller;

import com.wa.dto.NovaEscolaCalculationRequestDTO;
import com.wa.dto.ParcelaCalculadaDTO;
import com.wa.service.BcbCalculatorScraper;
import com.wa.service.EconomicIndexService;
import com.wa.service.ExcelGenerationService;
import com.wa.service.NovaEscolaCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calculations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CalculationController {
    
    private final NovaEscolaCalculationService calculationService;
    private final ExcelGenerationService excelGenerationService;
    private final EconomicIndexService economicIndexService;
    private final BcbCalculatorScraper bcbCalculatorScraper;
    
    @PostMapping("/novaescola")
    public ResponseEntity<ByteArrayResource> calculateNovaEscola(
            @Valid @RequestBody NovaEscolaCalculationRequestDTO request) {
        try {
            log.info("Gerando cálculo NOVAESCOLA - Valor base: R$ {}, IPCA-E: {}, SELIC: {}", 
                    request.getBaseValue(), request.getIpcaEFactor(), request.getSelicFactor());
            
            // Calcular parcelas usando os fatores fornecidos
            List<ParcelaCalculadaDTO> parcelas = calculationService.calcularParcelas(
                    BigDecimal.valueOf(request.getBaseValue()),
                    request.getIpcaEFactor(),
                    request.getSelicFactor()
            );
            
            // Gerar planilha Excel
            byte[] excelBytes = excelGenerationService.generateNovaEscolaExcel(parcelas);
            
            // Criar nome do arquivo
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("Calculo_NOVAESCOLA_%s.xlsx", timestamp);
            
            // Criar recurso para download
            ByteArrayResource resource = new ByteArrayResource(excelBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(excelBytes.length)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Erro ao gerar cálculo NOVAESCOLA: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Retorna o fator de correção IPCA-E fixo (2.88680560)
     */
    @GetMapping("/factors/ipcae")
    public ResponseEntity<Map<String, Object>> getIpcaEFactor(
            @RequestParam(required = false, defaultValue = "02/2003") String dataInicio) {
        try {
            // Valor fixo conforme especificação
            BigDecimal fator = new BigDecimal("2.88680560");
            
            Map<String, Object> response = new HashMap<>();
            response.put("fator", fator);
            response.put("dataInicio", dataInicio);
            response.put("dataFim", "30/11/2021");
            response.put("percentual", fator.subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao retornar fator IPCA-E: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtém o fator de correção SELIC da Calculadora do Cidadão do BCB (scraping)
     */
    @GetMapping("/factors/selic")
    public ResponseEntity<Map<String, Object>> getSelicFactor(
            @RequestParam(required = false, defaultValue = "09/12/2021") String dataInicio) {
        try {
            // Converter formato dd/MM/yyyy para LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataInicioDate = LocalDate.parse(dataInicio, formatter);
            LocalDate dataFim = LocalDate.now();
            
            // Usar scraping da Calculadora do Cidadão em vez da API
            BigDecimal fator = bcbCalculatorScraper.getSelicFactorFromCalculator(dataInicioDate, dataFim);
            
            if (fator == null) {
                log.error("Não foi possível obter fator SELIC da Calculadora do Cidadão");
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Não foi possível obter o fator SELIC da Calculadora do Cidadão"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("fator", fator);
            response.put("dataInicio", dataInicio);
            response.put("dataFim", dataFim.format(formatter));
            response.put("percentual", fator.subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter fator SELIC: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

