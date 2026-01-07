package com.wa.controller;

import com.wa.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/persons");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(response.isCommitted()).thenReturn(false);
    }

    @Test
    void testHandlePersonNotFoundException_ReturnsNotFound() {
        // Arrange
        PersonNotFoundException exception = new PersonNotFoundException(1L);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handlePersonNotFoundException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Cliente não encontrado com ID: 1", response.getBody().get("message"));
    }

    @Test
    void testHandleTaskNotFoundException_ReturnsNotFound() {
        // Arrange
        TaskNotFoundException exception = new TaskNotFoundException(1L);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleTaskNotFoundException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testHandleDocumentTemplateNotFoundException_ReturnsNotFound() {
        // Arrange
        DocumentTemplateNotFoundException exception = 
                new DocumentTemplateNotFoundException("Template não encontrado: test.docx");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleDocumentTemplateNotFoundException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testHandleProcessNotFoundException_ReturnsNotFound() {
        // Arrange
        ProcessNotFoundException exception = new ProcessNotFoundException(1L);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleProcessNotFoundException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testHandleInvalidCredentialsException_ReturnsUnauthorized() {
        // Arrange
        InvalidCredentialsException exception = new InvalidCredentialsException();

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleInvalidCredentialsException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Credenciais inválidas", response.getBody().get("message"));
    }

    @Test
    void testHandleUsernameAlreadyExistsException_ReturnsConflict() {
        // Arrange
        UsernameAlreadyExistsException exception = 
                new UsernameAlreadyExistsException("Username já está em uso: testuser");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleUsernameAlreadyExistsException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testHandleAddressNotFoundException_ReturnsNotFound() {
        // Arrange
        AddressNotFoundException exception = new AddressNotFoundException(1L);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleAddressNotFoundException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testHandleMethodArgumentNotValidException_ReturnsBadRequest() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("person", "cpf", "CPF inválido");
        FieldError fieldError2 = new FieldError("person", "email", "Email inválido");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValidException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertTrue(response.getBody().containsKey("errors"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertNotNull(errors);
        assertTrue(errors.containsKey("cpf"));
        assertTrue(errors.containsKey("email"));
    }

    @Test
    void testHandleConstraintViolationException_ReturnsBadRequest() {
        // Arrange
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(violation1.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("cpf");
        when(violation1.getMessage()).thenReturn("CPF inválido");
        
        violations.add(violation1);
        when(exception.getConstraintViolations()).thenReturn(violations);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolationException(
                exception, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertTrue(response.getBody().containsKey("errors"));
    }

    @Test
    void testHandleException_IgnoresSseRequest() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/statistics/process-status/stream");
        Exception exception = new RuntimeException("Test error");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleException(
                exception, request, this.response);

        // Assert
        assertNull(response); // Deve retornar null para requisições SSE
    }

    @Test
    void testHandleException_IgnoresCommittedResponse() {
        // Arrange
        when(response.isCommitted()).thenReturn(true);
        Exception exception = new RuntimeException("Test error");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleException(
                exception, request, this.response);

        // Assert
        assertNull(response); // Deve retornar null quando resposta já foi commitada
    }
}

