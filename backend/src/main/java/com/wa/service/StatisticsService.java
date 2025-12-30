package com.wa.service;

import com.wa.dto.*;
import com.wa.model.Process;
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
        
        // Totais
        dto.setTotalClients(personRepository.count());
        dto.setTotalProcesses(processRepository.count());
        dto.setTotalMatriculations(matriculationRepository.count());
        dto.setTotalMoviments(movimentRepository.count());
        
        // Processos por Tipo
        List<Process> allProcesses = processRepository.findAll();
        Map<String, Long> processesByTypeMap = allProcesses.stream()
                .filter(p -> p.getTipoProcesso() != null)
                .collect(Collectors.groupingBy(
                    Process::getTipoProcesso,
                    Collectors.counting()
                ));
        
        List<CountByTypeDTO> processesByType = processesByTypeMap.entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CountByTypeDTO::getType))
                .collect(Collectors.toList());
        dto.setProcessesByType(processesByType);
        
        // Processos por Comarca com subdivisão por Tipo
        Map<String, Map<String, Long>> comarcaTypeMap = allProcesses.stream()
                .filter(p -> p.getComarca() != null && p.getTipoProcesso() != null)
                .collect(Collectors.groupingBy(
                    Process::getComarca,
                    Collectors.groupingBy(
                        Process::getTipoProcesso,
                        Collectors.counting()
                    )
                ));
        
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
        
        // Processos por Status com subdivisão por Tipo
        Map<String, Map<String, Long>> statusTypeMap = allProcesses.stream()
                .filter(p -> p.getStatus() != null && p.getTipoProcesso() != null)
                .collect(Collectors.groupingBy(
                    Process::getStatus,
                    Collectors.groupingBy(
                        Process::getTipoProcesso,
                        Collectors.counting()
                    )
                ));
        
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
        
        // Total das Ações (soma dos valores de todos os processos)
        Double totalAcoes = allProcesses.stream()
                .filter(p -> p.getValor() != null)
                .mapToDouble(Process::getValor)
                .sum();
        dto.setTotalAcoes(totalAcoes);
        
        // Honorários por Tipo (calculando quando contratual estiver vazio)
        Map<String, List<Process>> processesByTipo = allProcesses.stream()
                .filter(p -> p.getTipoProcesso() != null)
                .collect(Collectors.groupingBy(Process::getTipoProcesso));
        
        Double totalHonorariosEsperados = 0.0;
        
        List<HonorariosByTypeDTO> honorariosByType = processesByTipo.entrySet().stream()
                .map(entry -> {
                    String tipo = entry.getKey();
                    List<Process> processos = entry.getValue();
                    
                    // Calcular honorários contratuais (considerando quando está vazio)
                    Double totalContratuais = processos.stream()
                            .mapToDouble(p -> {
                                if (p.getPrevisaoHonorariosContratuais() != null) {
                                    return p.getPrevisaoHonorariosContratuais();
                                } else if (p.getValor() != null && p.getTipoProcesso() != null) {
                                    // Calcular baseado no tipo: 30% para PISO, 20% para outros
                                    String tipoUpper = p.getTipoProcesso().toUpperCase();
                                    if ("PISO".equals(tipoUpper)) {
                                        return p.getValor() * 0.30;
                                    } else if ("NOVAESCOLA".equals(tipoUpper) || "INTERNIVEIS".equals(tipoUpper)) {
                                        return p.getValor() * 0.20;
                                    }
                                }
                                return 0.0;
                            })
                            .sum();
                    
                    // Calcular honorários sucumbenciais (considerando quando está vazio - 10% do valor)
                    Double totalSucumbenciais = processos.stream()
                            .mapToDouble(p -> {
                                if (p.getPrevisaoHonorariosSucumbenciais() != null) {
                                    return p.getPrevisaoHonorariosSucumbenciais();
                                } else if (p.getValor() != null) {
                                    // Calcular 10% do valor da ação quando sucumbenciais estiver vazio
                                    return p.getValor() * 0.10;
                                }
                                return 0.0;
                            })
                            .sum();
                    
                    Double total = totalContratuais + totalSucumbenciais;
                    
                    // Calcular total de ações por tipo
                    Double totalAcoesPorTipo = processos.stream()
                            .filter(p -> p.getValor() != null)
                            .mapToDouble(Process::getValor)
                            .sum();
                    
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
