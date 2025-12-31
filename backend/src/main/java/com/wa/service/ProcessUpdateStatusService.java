package com.wa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wa.dto.ProcessStatusDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class ProcessUpdateStatusService {
    
    public enum ProcessStatus {
        IDLE, RUNNING, COMPLETED, ERROR
    }
    
    @Getter
    public static class StatusInfo {
        private final ProcessStatus status;
        private final LocalDateTime startedAt;
        private final LocalDateTime completedAt;
        private final String errorMessage;
        
        public StatusInfo(ProcessStatus status, LocalDateTime startedAt, LocalDateTime completedAt, String errorMessage) {
            this.status = status;
            this.startedAt = startedAt;
            this.completedAt = completedAt;
            this.errorMessage = errorMessage;
        }
        
        public StatusInfo(ProcessStatus status, LocalDateTime startedAt, LocalDateTime completedAt) {
            this(status, startedAt, completedAt, null);
        }
    }
    
    private final AtomicReference<StatusInfo> currentStatus = new AtomicReference<>(
        new StatusInfo(ProcessStatus.IDLE, null, null)
    );
    
    private final List<org.springframework.web.servlet.mvc.method.annotation.SseEmitter> emitters = new ArrayList<>();
    private final ObjectMapper objectMapper;
    
    @Autowired
    public ProcessUpdateStatusService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Inicia o rastreamento do processamento
     */
    public void startProcessing() {
        LocalDateTime now = LocalDateTime.now();
        StatusInfo newStatus = new StatusInfo(ProcessStatus.RUNNING, now, null);
        StatusInfo oldStatus = currentStatus.getAndSet(newStatus);
        
        if (oldStatus.getStatus() == ProcessStatus.RUNNING) {
            log.warn("Tentativa de iniciar processamento quando já está em execução");
            throw new IllegalStateException("Processamento já está em execução");
        }
        
        log.info("Status de processamento alterado para RUNNING");
        notifyEmitters("RUNNING");
    }
    
    /**
     * Marca o processamento como concluído
     */
    public void completeProcessing() {
        LocalDateTime now = LocalDateTime.now();
        StatusInfo current = currentStatus.get();
        StatusInfo newStatus = new StatusInfo(ProcessStatus.COMPLETED, current.getStartedAt(), now);
        currentStatus.set(newStatus);
        
        log.info("Status de processamento alterado para COMPLETED");
        notifyEmitters("COMPLETED");
        
        // Limpar status após 5 minutos
        scheduleStatusCleanup();
    }
    
    /**
     * Marca o processamento como erro
     */
    public void errorProcessing(String errorMessage) {
        LocalDateTime now = LocalDateTime.now();
        StatusInfo current = currentStatus.get();
        StatusInfo newStatus = new StatusInfo(ProcessStatus.ERROR, current.getStartedAt(), now, errorMessage);
        currentStatus.set(newStatus);
        
        log.error("Status de processamento alterado para ERROR: {}", errorMessage);
        notifyEmitters("ERROR");
        
        // Limpar status após 5 minutos
        scheduleStatusCleanup();
    }
    
    /**
     * Retorna o status atual
     */
    public StatusInfo getCurrentStatus() {
        return currentStatus.get();
    }
    
    /**
     * Verifica se há processamento em andamento
     */
    public boolean isProcessing() {
        StatusInfo status = currentStatus.get();
        
        // Se está RUNNING há mais de 30 minutos, considera como travado e reseta
        if (status.getStatus() == ProcessStatus.RUNNING && status.getStartedAt() != null) {
            long minutesRunning = java.time.Duration.between(status.getStartedAt(), LocalDateTime.now()).toMinutes();
            if (minutesRunning > 30) {
                log.warn("Processamento RUNNING há mais de 30 minutos, resetando para IDLE");
                StatusInfo idleStatus = new StatusInfo(ProcessStatus.IDLE, null, null);
                currentStatus.set(idleStatus);
                return false;
            }
        }
        
        return status.getStatus() == ProcessStatus.RUNNING;
    }
    
    /**
     * Registra um emitter SSE para receber notificações
     */
    public synchronized void registerEmitter(org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter) {
        emitters.add(emitter);
        
        // Enviar status atual imediatamente
        StatusInfo status = currentStatus.get();
        try {
            ProcessStatusDTO dto = ProcessStatusDTO.fromStatusInfo(status);
            String jsonData = objectMapper.writeValueAsString(dto);
            emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                .name("status")
                .data(jsonData));
        } catch (Exception e) {
            if (isClientDisconnectedException(e)) {
                log.debug("Cliente desconectado ao enviar status inicial");
            } else {
                log.warn("Erro ao enviar status inicial para emitter: {}", e.getMessage());
            }
            removeEmitter(emitter);
        }
        
        // Remover emitter quando completar ou em caso de erro
        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError((ex) -> {
            if (!isClientDisconnectedException(ex)) {
                log.debug("Erro no emitter SSE: {}", ex.getMessage());
            }
            removeEmitter(emitter);
        });
    }
    
    /**
     * Remove um emitter da lista
     */
    private synchronized void removeEmitter(org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter) {
        emitters.remove(emitter);
    }
    
    /**
     * Notifica todos os emitters sobre mudança de status
     */
    private synchronized void notifyEmitters(String status) {
        List<org.springframework.web.servlet.mvc.method.annotation.SseEmitter> toRemove = new ArrayList<>();
        
        StatusInfo statusInfo = currentStatus.get();
        try {
            ProcessStatusDTO dto = ProcessStatusDTO.fromStatusInfo(statusInfo);
            String jsonData = objectMapper.writeValueAsString(dto);
            
            for (org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter : emitters) {
                try {
                    emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                        .name("status")
                        .data(jsonData));
                } catch (Exception e) {
                    if (isClientDisconnectedException(e)) {
                        // Cliente desconectou - isso é normal, não precisa logar como erro
                        log.debug("Cliente desconectado durante notificação SSE");
                    } else {
                        log.warn("Erro ao notificar emitter: {}", e.getMessage());
                    }
                    toRemove.add(emitter);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao serializar status para JSON", e);
        }
        
        emitters.removeAll(toRemove);
    }
    
    /**
     * Verifica se a exceção é causada por desconexão do cliente (Broken pipe, ClientAbortException, etc.)
     */
    private boolean isClientDisconnectedException(Throwable e) {
        if (e == null) {
            return false;
        }
        
        String message = e.getMessage();
        String className = e.getClass().getName();
        
        // Verificar por tipo de exceção
        if (e instanceof java.io.IOException) {
            if (message != null && (message.contains("Broken pipe") || 
                                    message.contains("Connection reset") ||
                                    message.contains("Connection closed"))) {
                return true;
            }
        }
        
        // Verificar por nome da classe
        if (className.contains("ClientAbortException") || 
            className.contains("BrokenPipeException") ||
            className.contains("ConnectionResetException")) {
            return true;
        }
        
        // Verificar causa recursivamente
        Throwable cause = e.getCause();
        if (cause != null && cause != e) {
            return isClientDisconnectedException(cause);
        }
        
        return false;
    }
    
    /**
     * Agenda limpeza do status após 5 minutos
     */
    private void scheduleStatusCleanup() {
        new Thread(() -> {
            try {
                Thread.sleep(5 * 60 * 1000); // 5 minutos
                StatusInfo current = currentStatus.get();
                if (current.getStatus() == ProcessStatus.COMPLETED || current.getStatus() == ProcessStatus.ERROR) {
                    StatusInfo idleStatus = new StatusInfo(ProcessStatus.IDLE, null, null);
                    currentStatus.set(idleStatus);
                    log.info("Status de processamento limpo e resetado para IDLE");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

