package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessFilterOptionsDTO {
    private List<String> comarcas;
    private List<String> varas;
    private List<String> temas;
}
