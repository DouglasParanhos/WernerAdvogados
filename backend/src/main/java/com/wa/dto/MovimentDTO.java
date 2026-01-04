package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentDTO {
    private Long id;
    private String descricao;
    private LocalDateTime date;
    private Long processId;
    private String processNumero;
    private String processComarca;
    private String processVara;
    private String processTipoProcesso;
    private String processMatriculationNumero;
    private String processStatus;
    private Boolean visibleToClient;
}
