package com.wa.controller;

import com.wa.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePersonNotFoundException(
            PersonNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Cliente não encontrado: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTaskNotFoundException(
            TaskNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Tarefa não encontrada: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(DocumentTemplateNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDocumentTemplateNotFoundException(
            DocumentTemplateNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Template não encontrado: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ProcessNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProcessNotFoundException(
            ProcessNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Processo não encontrado: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(
            InvalidCredentialsException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Credenciais inválidas: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Username já existe: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAddressNotFoundException(
            AddressNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Endereço não encontrado: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Erro de validação: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Erro de validação");
        
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null 
                                ? fieldError.getDefaultMessage() 
                                : "Erro de validação",
                        (existing, replacement) -> existing + "; " + replacement
                ));
        
        error.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (isSseRequest(request) || isResponseCommitted(response)) {
            return null;
        }
        
        log.warn("Violação de constraint: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Erro de validação");
        
        Map<String, String> violations = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage() != null 
                                ? violation.getMessage() 
                                : "Erro de validação",
                        (existing, replacement) -> existing + "; " + replacement
                ));
        
        error.put("errors", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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

