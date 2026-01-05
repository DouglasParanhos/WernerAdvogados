package com.wa.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(1) // Prioridade baixa para permitir que handlers específicos tenham prioridade
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Verifica se a requisição é para um endpoint SSE (Server-Sent Events)
     */
    private boolean isSseRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        
        String requestURI = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        
        // Verificar se é o endpoint de stream SSE
        if (requestURI != null && requestURI.contains("/process-status/stream")) {
            return true;
        }
        
        // Verificar se o Accept header contém text/event-stream
        if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica se a resposta já foi commitada (não pode mais ser modificada)
     */
    private boolean isResponseCommitted(HttpServletResponse response) {
        if (response == null) {
            return false;
        }
        
        try {
            return response.isCommitted();
        } catch (Exception e) {
            return false;
        }
    }
    
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException e, 
            HttpServletRequest request,
            HttpServletResponse response) {
        // Para timeouts em requisições assíncronas (SSE), apenas logar em nível debug
        // pois isso é esperado quando clientes desconectam ou há timeout
        if (isSseRequest(request)) {
            log.debug("Timeout em requisição SSE (cliente desconectado ou timeout): {}", e.getMessage());
            return;
        }
        log.debug("Timeout em requisição assíncrona: {}", e.getMessage());
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(
            AccessDeniedException e, 
            HttpServletRequest request,
            HttpServletResponse response) {
        
        // Não processar exceções em endpoints SSE ou quando a resposta já foi commitada
        if (isSseRequest(request) || isResponseCommitted(response)) {
            log.debug("AccessDeniedException em requisição SSE ou resposta já commitada, ignorando handler global: {}", 
                    e.getMessage());
            return null;
        }
        
        log.warn("Acesso negado: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage() != null ? e.getMessage() : "Acesso negado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException e, 
            HttpServletRequest request,
            HttpServletResponse response) {
        
        // Não processar AccessDeniedException aqui (já tratado acima)
        if (e instanceof AccessDeniedException) {
            return null;
        }
        
        // Não processar exceções em endpoints SSE ou quando a resposta já foi commitada
        // Nesses casos, deixar o Spring usar o comportamento padrão
        if (isSseRequest(request) || isResponseCommitted(response)) {
            log.debug("Exceção em requisição SSE ou resposta já commitada, ignorando handler global: {}", 
                    e.getMessage());
            // Retornar null faz o Spring continuar procurando outros handlers ou usar comportamento padrão
            return null;
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage() != null ? e.getMessage() : "Erro desconhecido");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(
            Exception e, 
            HttpServletRequest request,
            HttpServletResponse response) {
        
        // Não processar exceções em endpoints SSE ou quando a resposta já foi commitada
        // Nesses casos, deixar o Spring usar o comportamento padrão
        if (isSseRequest(request) || isResponseCommitted(response)) {
            log.debug("Exceção em requisição SSE ou resposta já commitada, ignorando handler global: {}", 
                    e.getMessage());
            // Retornar null faz o Spring continuar procurando outros handlers ou usar comportamento padrão
            return null;
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("message", "Erro interno do servidor: " + (e.getMessage() != null ? e.getMessage() : "Erro desconhecido"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

