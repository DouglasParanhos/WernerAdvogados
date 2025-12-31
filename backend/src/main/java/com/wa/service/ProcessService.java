package com.wa.service;

import com.wa.dto.MovimentDTO;
import com.wa.dto.ProcessDTO;
import com.wa.dto.ProcessRequestDTO;
import com.wa.model.Matriculation;
import com.wa.model.Moviment;
import com.wa.model.Process;
import com.wa.repository.MatriculationRepository;
import com.wa.repository.ProcessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessService {
    
    private final ProcessRepository processRepository;
    private final MatriculationRepository matriculationRepository;
    
    /**
     * Retorna o valor efetivo do processo: valorCorrigido se disponível, caso contrário valorOriginal
     */
    private Double getValorEfetivo(Process process) {
        return process.getValorCorrigido() != null ? process.getValorCorrigido() : process.getValorOriginal();
    }
    
    /**
     * Retorna o valor efetivo do request: valorCorrigido se disponível, caso contrário valorOriginal
     */
    private Double getValorEfetivo(ProcessRequestDTO request) {
        return request.getValorCorrigido() != null ? request.getValorCorrigido() : request.getValorOriginal();
    }
    
    public List<ProcessDTO> findAll() {
        return processRepository.findAllWithRelations().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProcessDTO> findByPersonId(Long personId) {
        return processRepository.findByPersonIdWithRelations(personId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ProcessDTO findById(Long id) {
        Process process = processRepository.findByIdWithMoviments(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + id));
        return convertToDTO(process);
    }
    
    @Transactional
    public ProcessDTO create(ProcessRequestDTO request) {
        Matriculation matriculation = matriculationRepository.findById(request.getMatriculationId())
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada com ID: " + request.getMatriculationId()));
        
        Process process = new Process();
        process.setNumero(request.getNumero());
        process.setComarca(request.getComarca());
        process.setVara(request.getVara());
        process.setSistema(request.getSistema());
        process.setValorOriginal(request.getValorOriginal());
        process.setValorCorrigido(request.getValorCorrigido());
        
        // Calcular honorários contratuais automaticamente se estiver vazio
        Double valorEfetivo = getValorEfetivo(request);
        Double honorariosContratuais = request.getPrevisaoHonorariosContratuais();
        if (honorariosContratuais == null && valorEfetivo != null && request.getTipoProcesso() != null) {
            if ("PISO".equalsIgnoreCase(request.getTipoProcesso())) {
                honorariosContratuais = valorEfetivo * 0.30;
            } else if ("NOVAESCOLA".equalsIgnoreCase(request.getTipoProcesso()) || 
                       "INTERNIVEIS".equalsIgnoreCase(request.getTipoProcesso())) {
                honorariosContratuais = valorEfetivo * 0.20;
            }
        }
        process.setPrevisaoHonorariosContratuais(honorariosContratuais);
        
        // Calcular honorários sucumbenciais automaticamente se estiver vazio (10% do valor da ação)
        Double honorariosSucumbenciais = request.getPrevisaoHonorariosSucumbenciais();
        if (honorariosSucumbenciais == null && valorEfetivo != null) {
            honorariosSucumbenciais = valorEfetivo * 0.10;
        }
        process.setPrevisaoHonorariosSucumbenciais(honorariosSucumbenciais);
        process.setDistribuidoEm(request.getDistribuidoEm());
        process.setTipoProcesso(request.getTipoProcesso() != null ? request.getTipoProcesso() : "");
        process.setStatus(request.getStatus());
        process.setMatriculation(matriculation);
        
        process = processRepository.save(process);
        return convertToDTO(processRepository.findByIdWithMoviments(process.getId()).orElse(process));
    }
    
    @Transactional
    public ProcessDTO update(Long id, ProcessRequestDTO request) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + id));
        
        Matriculation matriculation = matriculationRepository.findById(request.getMatriculationId())
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada com ID: " + request.getMatriculationId()));
        
        process.setNumero(request.getNumero());
        process.setComarca(request.getComarca());
        process.setVara(request.getVara());
        process.setSistema(request.getSistema());
        process.setValorOriginal(request.getValorOriginal());
        process.setValorCorrigido(request.getValorCorrigido());
        
        // Calcular honorários contratuais automaticamente se estiver vazio
        Double valorEfetivo = getValorEfetivo(request);
        Double honorariosContratuais = request.getPrevisaoHonorariosContratuais();
        if (honorariosContratuais == null && valorEfetivo != null && request.getTipoProcesso() != null) {
            if ("PISO".equalsIgnoreCase(request.getTipoProcesso())) {
                honorariosContratuais = valorEfetivo * 0.30;
            } else if ("NOVAESCOLA".equalsIgnoreCase(request.getTipoProcesso()) || 
                       "INTERNIVEIS".equalsIgnoreCase(request.getTipoProcesso())) {
                honorariosContratuais = valorEfetivo * 0.20;
            }
        }
        process.setPrevisaoHonorariosContratuais(honorariosContratuais);
        
        // Calcular honorários sucumbenciais automaticamente se estiver vazio (10% do valor da ação)
        Double honorariosSucumbenciais = request.getPrevisaoHonorariosSucumbenciais();
        if (honorariosSucumbenciais == null && valorEfetivo != null) {
            honorariosSucumbenciais = valorEfetivo * 0.10;
        }
        process.setPrevisaoHonorariosSucumbenciais(honorariosSucumbenciais);
        process.setDistribuidoEm(request.getDistribuidoEm());
        process.setTipoProcesso(request.getTipoProcesso());
        if (request.getStatus() != null) {
            process.setStatus(request.getStatus());
        }
        process.setMatriculation(matriculation);
        
        process = processRepository.save(process);
        return convertToDTO(processRepository.findByIdWithMoviments(process.getId()).orElse(process));
    }
    
    @Transactional
    public void delete(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + id));
        processRepository.delete(process);
    }
    
    public List<String> getDistinctStatuses() {
        return processRepository.findAll().stream()
                .map(Process::getStatus)
                .filter(status -> status != null && !status.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProcessDTO updateStatus(Long id, String status) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + id));
        process.setStatus(status != null && status.trim().isEmpty() ? null : status);
        process = processRepository.save(process);
        return convertToDTO(processRepository.findByIdWithMoviments(process.getId()).orElse(process));
    }
    
    private ProcessDTO convertToDTO(Process process) {
        ProcessDTO dto = new ProcessDTO();
        dto.setId(process.getId());
        dto.setNumero(process.getNumero());
        dto.setComarca(process.getComarca());
        dto.setVara(process.getVara());
        dto.setSistema(process.getSistema());
        dto.setValorOriginal(process.getValorOriginal());
        dto.setValorCorrigido(process.getValorCorrigido());
        dto.setPrevisaoHonorariosContratuais(process.getPrevisaoHonorariosContratuais());
        dto.setPrevisaoHonorariosSucumbenciais(process.getPrevisaoHonorariosSucumbenciais());
        dto.setDistribuidoEm(process.getDistribuidoEm());
        dto.setTipoProcesso(process.getTipoProcesso());
        dto.setStatus(process.getStatus());
        
        if (process.getMatriculation() != null) {
            dto.setMatriculationId(process.getMatriculation().getId());
        }
        
        if (process.getMoviments() != null) {
            dto.setMoviments(process.getMoviments().stream()
                    .map(this::convertMovimentToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private MovimentDTO convertMovimentToDTO(Moviment moviment) {
        MovimentDTO dto = new MovimentDTO();
        dto.setId(moviment.getId());
        dto.setDescricao(moviment.getDescricao());
        dto.setDate(moviment.getDate());
        dto.setProcessId(moviment.getProcess().getId());
        return dto;
    }
}

