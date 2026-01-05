package com.wa.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário temporário para gerar hashes BCrypt de senhas.
 * Execute este método main para gerar os hashes e depois delete este arquivo.
 * 
 * IMPORTANTE: Este arquivo é temporário e não deve ser commitado no git!
 */
public class GeneratePasswordHash {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password1 = "230623!";
        String password2 = "Trevas!";
        
        String hash1 = encoder.encode(password1);
        String hash2 = encoder.encode(password2);
        
        System.out.println("========================================");
        System.out.println("HASHES BCrypt gerados:");
        System.out.println("========================================");
        System.out.println("Usuário: liz.werner");
        System.out.println("Senha: " + password1);
        System.out.println("Hash: " + hash1);
        System.out.println();
        System.out.println("Usuário: angelo.masullo");
        System.out.println("Senha: " + password2);
        System.out.println("Hash: " + hash2);
        System.out.println("========================================");
        System.out.println();
        System.out.println("SQL para inserir os usuários:");
        System.out.println();
        System.out.println("INSERT INTO users (username, password, user_role, created_on, modified_on)");
        System.out.println("VALUES ('liz.werner', '" + hash1 + "', 'USER', NOW(), NOW())");
        System.out.println("ON CONFLICT (username) DO NOTHING;");
        System.out.println();
        System.out.println("INSERT INTO users (username, password, user_role, created_on, modified_on)");
        System.out.println("VALUES ('angelo.masullo', '" + hash2 + "', 'USER', NOW(), NOW())");
        System.out.println("ON CONFLICT (username) DO NOTHING;");
    }
}

