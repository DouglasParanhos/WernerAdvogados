package com.wa.service;

import com.wa.dto.AuthResponseDTO;
import com.wa.dto.LoginRequestDTO;
import com.wa.exception.InvalidCredentialsException;
import com.wa.model.User;
import com.wa.repository.UserRepository;
import com.wa.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole("ADMIN");

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void testAuthenticate_Success_ReturnsAuthResponse() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "ADMIN")).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtUtil, times(1)).generateToken("testuser", "ADMIN");
    }

    @Test
    void testAuthenticate_UserNotFound_ThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.authenticate(loginRequest);
        });

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void testAuthenticate_WrongPassword_ThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.authenticate(loginRequest);
        });

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void testAuthenticate_WithClientRole_ReturnsCorrectRole() {
        // Arrange
        user.setRole("CLIENT");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "CLIENT")).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("CLIENT", response.getRole());
        verify(jwtUtil, times(1)).generateToken("testuser", "CLIENT");
    }
}

