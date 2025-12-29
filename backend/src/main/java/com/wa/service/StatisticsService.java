package com.wa.service;

import com.wa.dto.CountByTypeDTO;
import com.wa.dto.HonorariosByTypeDTO;
import com.wa.dto.StatisticsDTO;
import com.wa.repository.PersonRepository;
import com.wa.repository.ProcessRepository;
import com.wa.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final ProcessRepository processRepository;
    private final TaskRepository taskRepository;
    private final PersonRepository personRepository;
    
    @Transactional
    public StatisticsDTO getStatistics() {
        StatisticsDTO stats = new StatisticsDTO();
        
        // Totais
        stats.setTotalProcesses(processRepository.count());
        stats.setTotalTasks(taskRepository.count());
        stats.setTotalClients(personRepository.count());
        
        // Processos por tipo
        stats.setProcessesByType(getProcessesByType());
        
        // Processos por comarca
        stats.setProcessesByComarca(getProcessesByComarca());
        
        // Processos por vara
        stats.setProcessesByVara(getProcessesByVara());
        
        // Processos por status
        stats.setProcessesByStatus(getProcessesByStatus());
        
        // Honorários por tipo
        stats.setHonorariosByType(getHonorariosByType());
        
        // Tarefas por status
        stats.setTasksByStatus(getTasksByStatus());
        
        // Tarefas por tipo
        stats.setTasksByType(getTasksByType());
        
        // Tarefas por responsável
        stats.setTasksByResponsavel(getTasksByResponsavel());
        
        // Total de honorários esperados
        stats.setTotalHonorariosEsperados(getTotalHonorariosEsperados());
        
        // Valor total dos processos
        stats.setTotalValorProcessos(getTotalValorProcessos());
        
        return stats;
    }
    
    private List<CountByTypeDTO> getProcessesByType() {
        return processRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getTipoProcesso() != null && !p.getTipoProcesso().trim().isEmpty() 
                        ? p.getTipoProcesso() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<CountByTypeDTO> getProcessesByComarca() {
        return processRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getComarca() != null && !p.getComarca().trim().isEmpty() 
                        ? p.getComarca() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<CountByTypeDTO> getProcessesByVara() {
        return processRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getVara() != null && !p.getVara().trim().isEmpty() 
                        ? p.getVara() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<CountByTypeDTO> getProcessesByStatus() {
        return processRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getStatus() != null && !p.getStatus().trim().isEmpty() 
                        ? p.getStatus() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<HonorariosByTypeDTO> getHonorariosByType() {
        return processRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getTipoProcesso() != null && !p.getTipoProcesso().trim().isEmpty() 
                        ? p.getTipoProcesso() 
                        : "Não informado"
                ))
                .entrySet().stream()
                .map(entry -> {
                    String tipo = entry.getKey();
                    List<com.wa.model.Process> processos = entry.getValue();
                    
                    Double totalContratuais = processos.stream()
                            .mapToDouble(p -> p.getPrevisaoHonorariosContratuais() != null 
                                ? p.getPrevisaoHonorariosContratuais() 
                                : 0.0)
                            .sum();
                    
                    Double totalSucumbenciais = processos.stream()
                            .mapToDouble(p -> p.getPrevisaoHonorariosSucumbenciais() != null 
                                ? p.getPrevisaoHonorariosSucumbenciais() 
                                : 0.0)
                            .sum();
                    
                    Double total = totalContratuais + totalSucumbenciais;
                    
                    return new HonorariosByTypeDTO(
                        tipo,
                        totalContratuais,
                        totalSucumbenciais,
                        total,
                        (long) processos.size()
                    );
                })
                .sorted((a, b) -> Double.compare(b.getTotalHonorarios(), a.getTotalHonorarios()))
                .collect(Collectors.toList());
    }
    
    private List<CountByTypeDTO> getTasksByStatus() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    t -> t.getStatus() != null && !t.getStatus().trim().isEmpty() 
                        ? t.getStatus() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<CountByTypeDTO> getTasksByType() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    t -> t.getTipoTarefa() != null && !t.getTipoTarefa().trim().isEmpty() 
                        ? t.getTipoTarefa() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private List<CountByTypeDTO> getTasksByResponsavel() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    t -> t.getResponsavel() != null && !t.getResponsavel().trim().isEmpty() 
                        ? t.getResponsavel() 
                        : "Não informado",
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new CountByTypeDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
    
    private Double getTotalHonorariosEsperados() {
        return processRepository.findAll().stream()
                .mapToDouble(p -> {
                    Double contratuais = p.getPrevisaoHonorariosContratuais() != null 
                        ? p.getPrevisaoHonorariosContratuais() 
                        : 0.0;
                    Double sucumbenciais = p.getPrevisaoHonorariosSucumbenciais() != null 
                        ? p.getPrevisaoHonorariosSucumbenciais() 
                        : 0.0;
                    return contratuais + sucumbenciais;
                })
                .sum();
    }
    
    private Double getTotalValorProcessos() {
        return processRepository.findAll().stream()
                .mapToDouble(p -> p.getValor() != null ? p.getValor() : 0.0)
                .sum();
    }
}

