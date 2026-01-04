package com.wa.service;

import com.wa.dto.*;
import com.wa.repository.MatriculationRepository;
import com.wa.repository.MovimentRepository;
import com.wa.repository.PersonRepository;
import com.wa.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final PersonRepository personRepository;
    private final ProcessRepository processRepository;
    private final MatriculationRepository matriculationRepository;
    private final MovimentRepository movimentRepository;
    
    public StatisticsDTO getStatistics() {
        StatisticsDTO dto = new StatisticsDTO();
        
        // Totais (já são queries agregadas, não precisam mudança)
        dto.setTotalClients(personRepository.count());
        dto.setTotalProcesses(processRepository.count());
        dto.setTotalMatriculations(matriculationRepository.count());
        dto.setTotalMoviments(movimentRepository.count());
        
        // Processos por Tipo - usando query agregada
        List<Object[]> processesByTypeData = processRepository.countByType();
        List<CountByTypeDTO> processesByType = processesByTypeData.stream()
                .map(row -> new CountByTypeDTO((String) row[0], ((Number) row[1]).longValue()))
                .sorted(Comparator.comparing(CountByTypeDTO::getType))
                .collect(Collectors.toList());
        dto.setProcessesByType(processesByType);
        
        // Processos por Comarca com subdivisão por Tipo - usando query agregada
        List<Object[]> comarcaTypeData = processRepository.countByComarcaAndType();
        Map<String, Map<String, Long>> comarcaTypeMap = new HashMap<>();
        for (Object[] row : comarcaTypeData) {
            String comarca = (String) row[0];
            String tipo = (String) row[1];
            Long count = ((Number) row[2]).longValue();
            comarcaTypeMap.computeIfAbsent(comarca, k -> new HashMap<>()).put(tipo, count);
        }
        
        List<ProcessByComarcaDTO> processesByComarca = comarcaTypeMap.entrySet().stream()
                .map(entry -> {
                    List<CountByTypeDTO> byType = entry.getValue().entrySet().stream()
                            .map(e -> new CountByTypeDTO(e.getKey(), e.getValue()))
                            .sorted(Comparator.comparing(CountByTypeDTO::getType))
                            .collect(Collectors.toList());
                    return new ProcessByComarcaDTO(entry.getKey(), byType);
                })
                .sorted(Comparator.comparing(ProcessByComarcaDTO::getComarca))
                .collect(Collectors.toList());
        dto.setProcessesByComarca(processesByComarca);
        
        // Processos por Status com subdivisão por Tipo - usando query agregada
        List<Object[]> statusTypeData = processRepository.countByStatusAndType();
        Map<String, Map<String, Long>> statusTypeMap = new HashMap<>();
        for (Object[] row : statusTypeData) {
            String status = (String) row[0];
            String tipo = (String) row[1];
            Long count = ((Number) row[2]).longValue();
            statusTypeMap.computeIfAbsent(status, k -> new HashMap<>()).put(tipo, count);
        }
        
        List<ProcessByStatusDTO> processesByStatus = statusTypeMap.entrySet().stream()
                .map(entry -> {
                    List<CountByTypeDTO> byType = entry.getValue().entrySet().stream()
                            .map(e -> new CountByTypeDTO(e.getKey(), e.getValue()))
                            .sorted(Comparator.comparing(CountByTypeDTO::getType))
                            .collect(Collectors.toList());
                    return new ProcessByStatusDTO(entry.getKey(), byType);
                })
                .sorted(Comparator.comparing(ProcessByStatusDTO::getStatus))
                .collect(Collectors.toList());
        dto.setProcessesByStatus(processesByStatus);
        
        // Total das Ações (soma dos valores ORIGINAIS) - usando query agregada
        Double totalAcoes = processRepository.sumValorOriginal();
        if (totalAcoes == null) {
            totalAcoes = 0.0;
        }
        dto.setTotalValorProcessos(totalAcoes);
        dto.setTotalAcoes(totalAcoes);
        
        // Total das Ações Corrigidas - usando query agregada
        Double totalAcoesCorrigido = processRepository.sumValorCorrigido();
        if (totalAcoesCorrigido == null) {
            totalAcoesCorrigido = 0.0;
        }
        dto.setTotalAcoesCorrigido(totalAcoesCorrigido);
        
        // Honorários por Tipo - precisa carregar dados específicos mas de forma otimizada
        // Usa query que retorna apenas campos necessários ao invés de objetos completos
        List<Object[]> honorariosData = processRepository.findProcessesForHonorarios();
        
        // Agrupar por tipo
        Map<String, List<Object[]>> processesByTipo = honorariosData.stream()
                .collect(Collectors.groupingBy(row -> (String) row[1]));
        
        Double totalHonorariosEsperados = 0.0;
        
        List<HonorariosByTypeDTO> honorariosByType = processesByTipo.entrySet().stream()
                .map(entry -> {
                    String tipo = entry.getKey();
                    List<Object[]> processos = entry.getValue();
                    
                    Double totalContratuais = 0.0;
                    Double totalSucumbenciais = 0.0;
                    Double totalAcoesPorTipo = 0.0;
                    
                    for (Object[] row : processos) {
                        Double valorOriginal = row[2] != null ? ((Number) row[2]).doubleValue() : null;
                        Double valorCorrigido = row[3] != null ? ((Number) row[3]).doubleValue() : null;
                        Double previsaoContratuais = row[4] != null ? ((Number) row[4]).doubleValue() : null;
                        Double previsaoSucumbenciais = row[5] != null ? ((Number) row[5]).doubleValue() : null;
                        
                        // Valor efetivo: valorCorrigido se disponível, caso contrário valorOriginal
                        Double valorEfetivo = valorCorrigido != null ? valorCorrigido : valorOriginal;
                        
                        // Calcular honorários contratuais
                        if (previsaoContratuais != null) {
                            totalContratuais += previsaoContratuais;
                        } else if (valorEfetivo != null && tipo != null) {
                            String tipoUpper = tipo.toUpperCase();
                            if ("PISO".equals(tipoUpper)) {
                                totalContratuais += valorEfetivo * 0.30;
                            } else if ("NOVAESCOLA".equals(tipoUpper) || "INTERNIVEIS".equals(tipoUpper)) {
                                totalContratuais += valorEfetivo * 0.20;
                            }
                        }
                        
                        // Calcular honorários sucumbenciais
                        if (previsaoSucumbenciais != null) {
                            totalSucumbenciais += previsaoSucumbenciais;
                        } else if (valorEfetivo != null) {
                            totalSucumbenciais += valorEfetivo * 0.10;
                        }
                        
                        // Total de ações por tipo
                        if (valorEfetivo != null) {
                            totalAcoesPorTipo += valorEfetivo;
                        }
                    }
                    
                    Double total = totalContratuais + totalSucumbenciais;
                    
                    HonorariosByTypeDTO honorarios = new HonorariosByTypeDTO();
                    honorarios.setTipoProcesso(tipo);
                    honorarios.setQuantidadeProcessos((long) processos.size());
                    honorarios.setTotalHonorariosContratuais(totalContratuais);
                    honorarios.setTotalHonorariosSucumbenciais(totalSucumbenciais);
                    honorarios.setTotalHonorarios(total);
                    honorarios.setTotalAcoes(totalAcoesPorTipo);
                    
                    return honorarios;
                })
                .sorted(Comparator.comparing(HonorariosByTypeDTO::getTipoProcesso))
                .collect(Collectors.toList());
        dto.setHonorariosByType(honorariosByType);
        
        // Calcular total de honorários esperados
        totalHonorariosEsperados = honorariosByType.stream()
                .mapToDouble(h -> h.getTotalHonorarios() != null ? h.getTotalHonorarios() : 0.0)
                .sum();
        dto.setTotalHonorariosEsperados(totalHonorariosEsperados);
        
        return dto;
    }
}
