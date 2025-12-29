package com.wa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String titulo;
    
    @Column(length = 1000)
    private String descricao;
    
    @Column(name = "tipo_tarefa", nullable = false, length = 50)
    private String tipoTarefa; // PRESENCIAL, COMUNICAR_CLIENTE, ESCRITA_PECA, PRAZO
    
    @Column(name = "status", nullable = false, length = 50)
    private String status; // PARA_INICIAR, EM_ANDAMENTO, COMPLETA
    
    @Column(name = "responsavel", nullable = false, length = 50)
    private String responsavel; // Liz, Angelo, Thiago
    
    @Column(name = "ordem")
    private Integer ordem;
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    
    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

