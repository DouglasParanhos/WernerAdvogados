package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursoDTO {
    private Long id;
    private Long processId;
    private String classe;
    private String numero;
    private String desembargadorRelator;
    private String camara;
    private String sistema;
    private String statusRecurso;
    private String historicoRelator;
    private String historicoCamara;
    private Boolean resp;
    private Boolean rext;
    private Boolean baixado;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
}
