package com.wa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "moviment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moviment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moviment_id")
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String descricao;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "process_id", nullable = false)
    private Process process;
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    
    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

