package com.wa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentGenerationRequestDTO {
    @NotNull(message = "ID do processo é obrigatório")
    private Long processId;
    
    @NotBlank(message = "Nome do template é obrigatório")
    private String templateName;
}

