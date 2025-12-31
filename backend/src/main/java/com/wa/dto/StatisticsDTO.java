package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long totalClients;
    private Long totalProcesses;
    private Long totalMatriculations;
    private Long totalMoviments;
    private Double totalAcoes;
    private Double totalHonorariosEsperados;
    private List<CountByTypeDTO> processesByType;
    private List<ProcessByComarcaDTO> processesByComarca;
    private List<ProcessByStatusDTO> processesByStatus;
    private List<HonorariosByTypeDTO> honorariosByType;
}
