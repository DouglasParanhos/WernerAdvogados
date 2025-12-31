package com.wa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NovaEscolaCalculationRequestDTO {
    
    private Integer grade;
    
    @NotNull(message = "Valor base é obrigatório")
    @Min(value = 100, message = "Valor base deve ser no mínimo R$ 100,00")
    private Double baseValue;
    
    @NotNull(message = "Fator IPCA-E é obrigatório")
    private BigDecimal ipcaEFactor;
    
    @NotNull(message = "Fator SELIC é obrigatório")
    private BigDecimal selicFactor;
}

