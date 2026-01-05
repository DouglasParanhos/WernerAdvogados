package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para retornar conteúdo do documento com dados do cliente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContentResponseDTO {
    private String templateName;
    private List<ContentBlock> content;
    private Map<String, String> clientData;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentBlock {
        private String text;
        private boolean isClientData;
        private Map<String, Object> formatting;
    }
}

