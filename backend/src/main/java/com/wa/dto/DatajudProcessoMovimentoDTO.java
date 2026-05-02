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
public class DatajudProcessoMovimentoDTO {

    /** NPU normalizado enviado à consulta */
    private String numeroProcesso;

    /** Id interno do Process (PK no WA), para link de detalhes */
    private Long processoId;

    /** encontrado | nao_encontrado | erro */
    private String status;

    private String erro;

    /** Movimentos por grau (G1, G2, …); processos sem andamento no periodo nao entram em {@code resultados}. */
    private List<DatajudGrauBlocoDTO> graus;
}
