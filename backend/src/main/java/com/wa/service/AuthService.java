package com.wa.service;

import com.wa.dto.AuthResponseDTO;
import com.wa.dto.LoginRequestDTO;
import com.wa.exception.InvalidCredentialsException;
import com.wa.model.User;
import com.wa.repository.UserRepository;
import com.wa.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public AuthResponseDTO authenticate(LoginRequestDTO loginRequest) {
        log.info("Tentativa de login para usuário: {}", loginRequest.getUsername());
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado: {}", loginRequest.getUsername());
                    return new InvalidCredentialsException();
                });
        
        log.debug("Usuário encontrado: {}, role: {}", user.getUsername(), user.getRole());
        log.debug("Hash no banco (primeiros 30 chars): {}", user.getPassword().substring(0, Math.min(30, user.getPassword().length())));
        log.debug("Tamanho da senha recebida: {}", loginRequest.getPassword() != null ? loginRequest.getPassword().length() : 0);
        
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        log.debug("Senha corresponde: {}", passwordMatches);
        
        if (!passwordMatches) {
            log.warn("Senha incorreta para usuário: {}", loginRequest.getUsername());
            throw new InvalidCredentialsException();
        }
        
        log.info("Login bem-sucedido para usuário: {}", loginRequest.getUsername());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        return new AuthResponseDTO(token, user.getUsername(), user.getRole());
    }
}

