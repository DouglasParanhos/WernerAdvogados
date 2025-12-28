package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplateDTO {
    private String name;
    private String type;  // PISO, INTERNIVEIS, NOVAESCOLA, etc.
    private String fileName;
    private String category;  // PROCESS or CLIENT
    private String relativePath;  // Path relative to documents/ folder
}

