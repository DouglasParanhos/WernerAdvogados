package com.wa.controller;

import com.wa.dto.ProcessStatusDTO;
import com.wa.dto.StatisticsDTO;
import com.wa.service.ProcessUpdateStatusService;
import com.wa.service.ProcessValueCorrectionService;
import com.wa.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    private final ProcessValueCorrectionService processValueCorrectionService;
    private final ProcessUpdateStatusService processUpdateStatusService;
    
    @GetMapping
    public ResponseEntity<StatisticsDTO> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    
    @PostMapping("/update-process-values")
    public ResponseEntity<Map<String, String>> updateProcessValues() {
        Map<String, String> response = new HashMap<>();
        
        // Verificar se já há processamento em andamento
        if (processUpdateStatusService.isProcessing()) {
            response.put("error", "Já existe um processamento em andamento");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            processValueCorrectionService.recalculateAllProcessValues();
            response.put("message", "Atualização de valores corrigidos iniciada com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Erro ao iniciar atualização: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/process-status")
    public ResponseEntity<ProcessStatusDTO> getProcessStatus() {
        ProcessUpdateStatusService.StatusInfo statusInfo = processUpdateStatusService.getCurrentStatus();
        return ResponseEntity.ok(ProcessStatusDTO.fromStatusInfo(statusInfo));
    }
    
    @GetMapping(value = "/process-status/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProcessStatus() {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutos de timeout para processamentos longos
        try {
            processUpdateStatusService.registerEmitter(emitter);
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}









