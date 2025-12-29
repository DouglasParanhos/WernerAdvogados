package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer ordem;
}

