package com.wa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequestDTO {
    @NotBlank(message = "Número do processo é obrigatório")
    private String numero;
    
    @NotBlank(message = "Comarca é obrigatória")
    private String comarca;
    
    @NotBlank(message = "Vara é obrigatória")
    private String vara;
    
    @NotBlank(message = "Sistema é obrigatório")
    private String sistema;
    
    private Double valor;
    private Double previsaoHonorariosContratuais;
    private Double previsaoHonorariosSucumbenciais;
    private LocalDateTime distribuidoEm;
    
    @NotNull(message = "ID da matrícula é obrigatório")
    private Long matriculationId;
}

