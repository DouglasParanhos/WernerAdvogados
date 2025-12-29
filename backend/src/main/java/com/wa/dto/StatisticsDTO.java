package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long totalProcesses;
    private Long totalTasks;
    private Long totalClients;
    
    // Processos por tipo
    private List<CountByTypeDTO> processesByType;
    
    // Processos por comarca
    private List<CountByTypeDTO> processesByComarca;
    
    // Processos por vara
    private List<CountByTypeDTO> processesByVara;
    
    // Processos por status
    private List<CountByTypeDTO> processesByStatus;
    
    // Valor esperado de honorários por tipo de processo
    private List<HonorariosByTypeDTO> honorariosByType;
    
    // Tarefas por status
    private List<CountByTypeDTO> tasksByStatus;
    
    // Tarefas por tipo
    private List<CountByTypeDTO> tasksByType;
    
    // Tarefas por responsável
    private List<CountByTypeDTO> tasksByResponsavel;
    
    // Total de honorários esperados (contratuais + sucumbenciais)
    private Double totalHonorariosEsperados;
    
    // Valor total dos processos
    private Double totalValorProcessos;
}

