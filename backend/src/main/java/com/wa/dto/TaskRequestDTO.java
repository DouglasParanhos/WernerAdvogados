package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String tipoTarefa;
    private String status;
    private String responsavel;
    private Long processId;
    private Integer ordem;
    private LocalDate prazoFinal;
}

