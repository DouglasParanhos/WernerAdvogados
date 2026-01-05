package com.wa.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber conteúdo editado do Quill Delta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContentDTO {
    @NotNull(message = "ID da pessoa é obrigatório")
    private Long personId;
    
    @NotBlank(message = "Nome do template é obrigatório")
    private String templateName;
    
    @NotNull(message = "Conteúdo é obrigatório")
    private JsonNode content; // Quill Delta format
}

