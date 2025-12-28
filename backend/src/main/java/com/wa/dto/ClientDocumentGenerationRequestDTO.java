package com.wa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocumentGenerationRequestDTO {
    @NotNull(message = "ID da pessoa é obrigatório")
    private Long personId;
    
    @NotBlank(message = "Nome do template é obrigatório")
    private String templateName;
}

