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
public class MatriculationRequestDTO {
    @NotBlank(message = "Número é obrigatório")
    private String numero;
    
    @NotNull(message = "Início ERJ é obrigatório")
    private LocalDateTime inicioErj;
    
    @NotBlank(message = "Cargo é obrigatório")
    private String cargo;
    
    @NotNull(message = "Data de aposentadoria é obrigatória")
    private LocalDateTime dataAposentadoria;
    
    @NotBlank(message = "Nível atual é obrigatório")
    private String nivelAtual;
    
    @NotBlank(message = "Triênio atual é obrigatório")
    private String trienioAtual;
    
    @NotBlank(message = "Referência é obrigatória")
    private String referencia;
    
    @NotNull(message = "ID da pessoa é obrigatório")
    private Long personId;
}

