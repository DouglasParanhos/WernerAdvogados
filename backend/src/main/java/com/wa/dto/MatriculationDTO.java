package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculationDTO {
    private Long id;
    private String numero;
    private LocalDateTime inicioErj;
    private String cargo;
    private LocalDateTime dataAposentadoria;
    private String nivelAtual;
    private String trienioAtual;
    private String referencia;
    private Long personId;
    private List<ProcessDTO> processes;
}

