package com.wa.service;

import com.wa.dto.CountByTypeDTO;
import com.wa.dto.HonorariosByTypeDTO;
import com.wa.dto.StatisticsDTO;
import com.wa.model.Process;
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
    
    // Helper method para verificar se um processo está arquivado
    private boolean isArchived(Process process) {
        String status = process.getStatus();
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return status.toLowerCase().contains("arquivado");
    }
    
    // Helper method para obter processos não arquivados
    private List<Process> getActiveProcesses() {
        return processRepository.findAll().stream()
                .filter(p -> !isArchived(p))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public StatisticsDTO getStatistics() {
        StatisticsDTO stats = new StatisticsDTO();
        List<Process> activeProcesses = getActiveProcesses();
        
        // Totais (apenas processos não arquivados)
        stats.setTotalProcesses((long) activeProcesses.size());
        stats.setTotalTasks(taskRepository.count());
        stats.setTotalClients(personRepository.count());
        
        // Processos por tipo
        stats.setProcessesByType(getProcessesByType(activeProcesses));
        
        // Processos por comarca
        stats.setProcessesByComarca(getProcessesByComarca(activeProcesses));
        
        // Processos por status
        stats.setProcessesByStatus(getProcessesByStatus(activeProcesses));
        
        // Honorários por tipo
        stats.setHonorariosByType(getHonorariosByType(activeProcesses));
        
        // Tarefas por status
        stats.setTasksByStatus(getTasksByStatus());
        
        // Tarefas por tipo
        stats.setTasksByType(getTasksByType());
        
        // Tarefas por responsável
        stats.setTasksByResponsavel(getTasksByResponsavel());
        
        // Total de honorários esperados
        stats.setTotalHonorariosEsperados(getTotalHonorariosEsperados(activeProcesses));
        
        // Valor total dos processos
        stats.setTotalValorProcessos(getTotalValorProcessos(activeProcesses));
        
        return stats;
    }
    
    private List<CountByTypeDTO> getProcessesByType(List<Process> processes) {
        return processes.stream()
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
    
    private List<CountByTypeDTO> getProcessesByComarca(List<Process> processes) {
        return processes.stream()
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
    
    private List<CountByTypeDTO> getProcessesByStatus(List<Process> processes) {
        return processes.stream()
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
    
    private List<HonorariosByTypeDTO> getHonorariosByType(List<Process> processes) {
        return processes.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getTipoProcesso() != null && !p.getTipoProcesso().trim().isEmpty() 
                        ? p.getTipoProcesso() 
                        : "Não informado"
                ))
                .entrySet().stream()
                .map(entry -> {
                    String tipo = entry.getKey();
                    List<Process> processos = entry.getValue();
                    
                    Double totalContratuais = processos.stream()
                            .mapToDouble(p -> p.getPrevisaoHonorariosContratuais() != null 
                                ? p.getPrevisaoHonorariosContratuais() 
                                : 0.0)
                            .sum();
                    
                    // Calcular honorários sucumbenciais: se não informado, usar 10% do valor principal
                    Double totalSucumbenciais = processos.stream()
                            .mapToDouble(p -> {
                                if (p.getPrevisaoHonorariosSucumbenciais() != null) {
                                    return p.getPrevisaoHonorariosSucumbenciais();
                                } else {
                                    // 10% do valor principal da ação
                                    Double valor = p.getValor();
                                    return valor != null ? valor * 0.10 : 0.0;
                                }
                            })
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
    
    private Double getTotalHonorariosEsperados(List<Process> processes) {
        return processes.stream()
                .mapToDouble(p -> {
                    Double contratuais = p.getPrevisaoHonorariosContratuais() != null 
                        ? p.getPrevisaoHonorariosContratuais() 
                        : 0.0;
                    // Se não informado, usar 10% do valor principal
                    Double sucumbenciais = p.getPrevisaoHonorariosSucumbenciais() != null 
                        ? p.getPrevisaoHonorariosSucumbenciais() 
                        : (p.getValor() != null ? p.getValor() * 0.10 : 0.0);
                    return contratuais + sucumbenciais;
                })
                .sum();
    }
    
    private Double getTotalValorProcessos(List<Process> processes) {
        return processes.stream()
                .mapToDouble(p -> p.getValor() != null ? p.getValor() : 0.0)
                .sum();
    }
}

