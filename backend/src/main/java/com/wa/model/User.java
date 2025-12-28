package com.wa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    
    @Column(unique = true, nullable = false, length = 255)
    private String username;
    
    @Column(nullable = false, length = 512)
    private String password;
    
    @Column(name = "user_role", nullable = false, length = 16)
    private String role = "USER";
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    
    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

