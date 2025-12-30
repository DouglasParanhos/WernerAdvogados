package com.wa.service;

import com.wa.dto.AuthResponseDTO;
import com.wa.dto.LoginRequestDTO;
import com.wa.model.User;
import com.wa.repository.UserRepository;
import com.wa.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public AuthResponseDTO authenticate(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        return new AuthResponseDTO(token, user.getUsername(), user.getRole());
    }
}

