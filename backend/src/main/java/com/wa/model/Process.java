package com.wa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "process")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String numero;
    
    @Column(nullable = false, length = 255)
    private String comarca;
    
    @Column(nullable = false, length = 255)
    private String vara;
    
    @Column(nullable = false, length = 255)
    private String sistema;
    
    @Column(name = "valor_original")
    private Double valorOriginal;
    
    @Column(name = "valor_corrigido")
    private Double valorCorrigido;
    
    @Column(name = "previsao_honorarios_contratuais")
    private Double previsaoHonorariosContratuais;
    
    @Column(name = "previsao_honorarios_sucumbenciais")
    private Double previsaoHonorariosSucumbenciais;
    
    @Column(name = "distribuido_em")
    private LocalDateTime distribuidoEm;
    
    @Column(name = "tipo_processo", nullable = false, length = 50)
    private String tipoProcesso;
    
    @Column(name = "status", length = 50)
    private String status;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "matriculation_id", referencedColumnName = "matriculation_id", nullable = false)
    private Matriculation matriculation;
    
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Moviment> moviments;
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    
    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

