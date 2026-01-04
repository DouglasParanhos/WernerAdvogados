package com.wa.job;

import com.wa.service.ProcessValueCorrectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessValueCorrectionJob {
    
    private final ProcessValueCorrectionService processValueCorrectionService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Job agendado para executar diariamente às 4h da manhã
     * Cron expression: segundo minuto hora dia mês dia-da-semana
     * 0 0 4 * * * = às 4:00:00 todos os dias
     * 
     * DESABILITADO: Agendamento comentado para não executar automaticamente
     */
    // @Scheduled(cron = "0 0 4 * * *", zone = "America/Sao_Paulo")
    public void executeDailyCorrection() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("========================================");
        log.info("Iniciando job de correção de valores às {}", startTime.format(DATE_TIME_FORMATTER));
        log.info("========================================");
        
        try {
            processValueCorrectionService.recalculateAllProcessValues();
            
            LocalDateTime endTime = LocalDateTime.now();
            long durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
            
            log.info("========================================");
            log.info("Job de correção de valores concluído às {}", endTime.format(DATE_TIME_FORMATTER));
            log.info("Duração total: {} segundos", durationSeconds);
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("Erro fatal ao executar job de correção de valores: {}", e.getMessage(), e);
        }
    }
}

