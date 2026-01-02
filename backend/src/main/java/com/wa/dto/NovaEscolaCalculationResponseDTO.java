package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovaEscolaCalculationResponseDTO {
    private String calculationId;
    private BigDecimal totalGeralDevido;
    private List<ParcelaCalculadaDTO> parcelas;
}


