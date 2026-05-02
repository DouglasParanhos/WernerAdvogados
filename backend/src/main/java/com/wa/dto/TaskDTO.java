package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String tipoTarefa;
    private String status;
    private String responsavel;
    private Long processId;
    private String processNumero;
    private LocalDate prazoFinal;
    private Integer ordem;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
}




