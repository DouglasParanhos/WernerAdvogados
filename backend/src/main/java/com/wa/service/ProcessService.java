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
    
    public List<ProcessDTO> findAll() {
        return processRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProcessDTO> findByPersonId(Long personId) {
        return processRepository.findByPersonId(personId).stream()
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
        process.setValor(request.getValor());
        process.setPrevisaoHonorariosContratuais(request.getPrevisaoHonorariosContratuais());
        process.setPrevisaoHonorariosSucumbenciais(request.getPrevisaoHonorariosSucumbenciais());
        process.setDistribuidoEm(request.getDistribuidoEm());
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
        process.setValor(request.getValor());
        process.setPrevisaoHonorariosContratuais(request.getPrevisaoHonorariosContratuais());
        process.setPrevisaoHonorariosSucumbenciais(request.getPrevisaoHonorariosSucumbenciais());
        process.setDistribuidoEm(request.getDistribuidoEm());
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
    
    private ProcessDTO convertToDTO(Process process) {
        ProcessDTO dto = new ProcessDTO();
        dto.setId(process.getId());
        dto.setNumero(process.getNumero());
        dto.setComarca(process.getComarca());
        dto.setVara(process.getVara());
        dto.setSistema(process.getSistema());
        dto.setValor(process.getValor());
        dto.setPrevisaoHonorariosContratuais(process.getPrevisaoHonorariosContratuais());
        dto.setPrevisaoHonorariosSucumbenciais(process.getPrevisaoHonorariosSucumbenciais());
        dto.setDistribuidoEm(process.getDistribuidoEm());
        dto.setTipoProcesso(process.getTipoProcesso());
        dto.setStatus(process.getStatus());
        dto.setMatriculationId(process.getMatriculation().getId());
        
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

