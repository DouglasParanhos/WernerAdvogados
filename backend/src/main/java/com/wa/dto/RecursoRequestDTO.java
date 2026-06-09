package com.wa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursoRequestDTO {

    @NotNull(message = "ID do processo é obrigatório")
    private Long processId;

    @NotBlank(message = "Classe é obrigatória")
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
}
