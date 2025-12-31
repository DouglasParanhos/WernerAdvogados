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

    // Processos por tipo
    private List<CountByTypeDTO> processesByType;

    // Processos por comarca
    private List<ProcessByComarcaDTO> processesByComarca;

    // Processos por status
    private List<ProcessByStatusDTO> processesByStatus;

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

    // Total das ações (alias para totalValorProcessos)
    private Double totalAcoes;

    // Total das ações corrigidas (soma apenas dos valores corrigidos)
    private Double totalAcoesCorrigido;
}
