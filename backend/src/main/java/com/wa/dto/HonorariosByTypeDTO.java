package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HonorariosByTypeDTO {
    private String tipoProcesso;
    private Double totalHonorariosContratuais;
    private Double totalHonorariosSucumbenciais;
    private Double totalHonorarios;
    private Long quantidadeProcessos;
}





