package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDTO {
    private Long id;
    private String numero;
    private String comarca;
    private String vara;
    private String sistema;
    private Double valorOriginal;
    private Double valorCorrigido;
    private Double previsaoHonorariosContratuais;
    private Double previsaoHonorariosSucumbenciais;
    private LocalDateTime distribuidoEm;
    private String tipoProcesso;
    private String status;
    private Long matriculationId;
    private MatriculationDTO matriculation;
    private List<MovimentDTO> moviments;
}

