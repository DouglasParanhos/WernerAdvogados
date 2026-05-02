package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatajudMovimentoConsultaResponseDTO {

    private LocalIntervaloDTO intervalo;
    private int totalConsultados;
    private int totalEncontrados;
    private int totalNaoEncontrados;
    private int totalErros;
    private List<DatajudProcessoMovimentoDTO> resultados;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocalIntervaloDTO {
        private String dataInicio;
        private String ate;
        private String zona;
    }
}
