package com.wa.service;

import com.wa.dto.MatriculationDTO;
import com.wa.dto.MatriculationRequestDTO;
import com.wa.dto.ProcessDTO;
import com.wa.model.Matriculation;
import com.wa.model.Person;
import com.wa.repository.MatriculationRepository;
import com.wa.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatriculationService {
    
    private final MatriculationRepository matriculationRepository;
    private final PersonRepository personRepository;
    
    public List<MatriculationDTO> findAll() {
        return matriculationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MatriculationDTO> findByPersonId(Long personId) {
        return matriculationRepository.findByPersonId(personId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public MatriculationDTO findById(Long id) {
        Matriculation matriculation = matriculationRepository.findByIdWithProcesses(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada com ID: " + id));
        return convertToDTO(matriculation);
    }
    
    @Transactional
    public MatriculationDTO create(MatriculationRequestDTO request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com ID: " + request.getPersonId()));
        
        Matriculation matriculation = new Matriculation();
        matriculation.setNumero(request.getNumero());
        matriculation.setInicioErj(request.getInicioErj());
        matriculation.setCargo(request.getCargo());
        matriculation.setDataAposentadoria(request.getDataAposentadoria());
        matriculation.setNivelAtual(request.getNivelAtual());
        matriculation.setTrienioAtual(request.getTrienioAtual());
        matriculation.setReferencia(request.getReferencia());
        matriculation.setPerson(person);
        
        matriculation = matriculationRepository.save(matriculation);
        return convertToDTO(matriculationRepository.findByIdWithProcesses(matriculation.getId()).orElse(matriculation));
    }
    
    @Transactional
    public MatriculationDTO update(Long id, MatriculationRequestDTO request) {
        Matriculation matriculation = matriculationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada com ID: " + id));
        
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com ID: " + request.getPersonId()));
        
        matriculation.setNumero(request.getNumero());
        matriculation.setInicioErj(request.getInicioErj());
        matriculation.setCargo(request.getCargo());
        matriculation.setDataAposentadoria(request.getDataAposentadoria());
        matriculation.setNivelAtual(request.getNivelAtual());
        matriculation.setTrienioAtual(request.getTrienioAtual());
        matriculation.setReferencia(request.getReferencia());
        matriculation.setPerson(person);
        
        matriculation = matriculationRepository.save(matriculation);
        return convertToDTO(matriculationRepository.findByIdWithProcesses(matriculation.getId()).orElse(matriculation));
    }
    
    @Transactional
    public void delete(Long id) {
        Matriculation matriculation = matriculationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada com ID: " + id));
        matriculationRepository.delete(matriculation);
    }
    
    private MatriculationDTO convertToDTO(Matriculation matriculation) {
        MatriculationDTO dto = new MatriculationDTO();
        dto.setId(matriculation.getId());
        dto.setNumero(matriculation.getNumero());
        dto.setInicioErj(matriculation.getInicioErj());
        dto.setCargo(matriculation.getCargo());
        dto.setDataAposentadoria(matriculation.getDataAposentadoria());
        dto.setNivelAtual(matriculation.getNivelAtual());
        dto.setTrienioAtual(matriculation.getTrienioAtual());
        dto.setReferencia(matriculation.getReferencia());
        dto.setPersonId(matriculation.getPerson().getId());
        
        if (matriculation.getProcesses() != null) {
            dto.setProcesses(matriculation.getProcesses().stream()
                    .map(this::convertProcessToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private ProcessDTO convertProcessToDTO(com.wa.model.Process process) {
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
        dto.setMatriculationId(process.getMatriculation().getId());
        return dto;
    }
}

