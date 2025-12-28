package com.wa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matriculation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matriculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matriculation_id")
    private Long id;
    
    @Column(nullable = false, length = 32)
    private String numero;
    
    @Column(name = "inicio_erj", nullable = true)
    private LocalDateTime inicioErj;
    
    @Column(unique = true, nullable = false, length = 255)
    private String cargo;
    
    @Column(name = "data_aposentadoria", nullable = true)
    private LocalDateTime dataAposentadoria;
    
    @Column(name = "nivel_atual", nullable = false)
    private String nivelAtual;
    
    @Column(name = "trienio_atual", nullable = true)
    private String trienioAtual;
    
    @Column(nullable = false)
    private String referencia;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", nullable = false)
    private Person person;
    
    @OneToMany(mappedBy = "matriculation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Process> processes;
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    
    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

