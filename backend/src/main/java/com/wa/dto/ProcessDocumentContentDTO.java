package com.wa.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber conteúdo editado do Quill Delta para processos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDocumentContentDTO {
    @NotNull(message = "ID do processo é obrigatório")
    private Long processId;
    
    @NotBlank(message = "Nome do template é obrigatório")
    private String templateName;
    
    @NotNull(message = "Conteúdo é obrigatório")
    private JsonNode content; // Quill Delta format
}

