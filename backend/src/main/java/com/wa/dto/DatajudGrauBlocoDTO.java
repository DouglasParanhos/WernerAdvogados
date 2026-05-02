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
public class DatajudGrauBlocoDTO {

    /** Ex.: G1, G2 ou rotulo quando ausente no indice */
    private String grau;

    private List<DatajudMovimentoItemDTO> movimentos;

    /** Metadados opcionais do _source Datajud */
    private String classeProcessual;

    private String orgaoJulgador;
}
