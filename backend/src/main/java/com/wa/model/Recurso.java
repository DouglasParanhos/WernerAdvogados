package com.wa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recurso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurso_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "process_id", nullable = false)
    private Process process;

    /** APELACAO ou AGRAVO_DE_INSTRUMENTO */
    @Column(nullable = false, length = 50)
    private String classe;

    /** Número do processo de 2ª instância (apenas para Agravo de Instrumento) */
    @Column(length = 100)
    private String numero;

    @Column(name = "desembargador_relator", length = 255)
    private String desembargadorRelator;

    @Column(length = 255)
    private String camara;

    /** TJRJ ou EPROC */
    @Column(length = 50)
    private String sistema;

    @Column(name = "status_recurso", length = 255)
    private String statusRecurso;

    /** PRO_PROFESSOR, CONTRA_PROFESSOR ou NA */
    @Column(name = "historico_relator", nullable = false, length = 30)
    private String historicoRelator = "NA";

    /** PRO_PROFESSOR, CONTRA_PROFESSOR ou NA */
    @Column(name = "historico_camara", nullable = false, length = 30)
    private String historicoCamara = "NA";

    @Column(nullable = false)
    private Boolean resp = false;

    @Column(nullable = false)
    private Boolean rext = false;

    @Column(nullable = false)
    private Boolean baixado = false;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}
