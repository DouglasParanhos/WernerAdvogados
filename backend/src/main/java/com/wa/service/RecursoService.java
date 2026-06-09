package com.wa.service;

import com.wa.dto.RecursoDTO;
import com.wa.dto.RecursoRequestDTO;
import com.wa.model.Process;
import com.wa.model.Recurso;
import com.wa.repository.ProcessRepository;
import com.wa.repository.RecursoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final ProcessRepository processRepository;

    public List<RecursoDTO> findByProcessId(Long processId) {
        return recursoRepository.findByProcessId(processId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RecursoDTO findById(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado com ID: " + id));
        return convertToDTO(recurso);
    }

    @Transactional
    public RecursoDTO create(RecursoRequestDTO request) {
        Process process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + request.getProcessId()));

        Recurso recurso = new Recurso();
        recurso.setProcess(process);
        applyFields(recurso, request);

        recurso = recursoRepository.save(recurso);
        return convertToDTO(recurso);
    }

    @Transactional
    public RecursoDTO update(Long id, RecursoRequestDTO request) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado com ID: " + id));

        applyFields(recurso, request);

        recurso = recursoRepository.save(recurso);
        return convertToDTO(recurso);
    }

    @Transactional
    public RecursoDTO baixar(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado com ID: " + id));
        recurso.setBaixado(true);
        recurso = recursoRepository.save(recurso);
        return convertToDTO(recurso);
    }

    @Transactional
    public void delete(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado com ID: " + id));
        recursoRepository.delete(recurso);
    }

    private void applyFields(Recurso recurso, RecursoRequestDTO request) {
        recurso.setClasse(request.getClasse());
        recurso.setNumero(request.getNumero());
        recurso.setDesembargadorRelator(request.getDesembargadorRelator());
        recurso.setCamara(request.getCamara());
        recurso.setSistema(request.getSistema());
        recurso.setStatusRecurso(request.getStatusRecurso());
        recurso.setHistoricoRelator(request.getHistoricoRelator() != null ? request.getHistoricoRelator() : "NA");
        recurso.setHistoricoCamara(request.getHistoricoCamara() != null ? request.getHistoricoCamara() : "NA");
        recurso.setResp(request.getResp() != null ? request.getResp() : false);
        recurso.setRext(request.getRext() != null ? request.getRext() : false);
        if (request.getBaixado() != null) {
            recurso.setBaixado(request.getBaixado());
        }
    }

    public RecursoDTO convertToDTO(Recurso recurso) {
        RecursoDTO dto = new RecursoDTO();
        dto.setId(recurso.getId());
        dto.setProcessId(recurso.getProcess().getId());
        dto.setClasse(recurso.getClasse());
        dto.setNumero(recurso.getNumero());
        dto.setDesembargadorRelator(recurso.getDesembargadorRelator());
        dto.setCamara(recurso.getCamara());
        dto.setSistema(recurso.getSistema());
        dto.setStatusRecurso(recurso.getStatusRecurso());
        dto.setHistoricoRelator(recurso.getHistoricoRelator());
        dto.setHistoricoCamara(recurso.getHistoricoCamara());
        dto.setResp(recurso.getResp());
        dto.setRext(recurso.getRext());
        dto.setBaixado(recurso.getBaixado());
        dto.setCreatedOn(recurso.getCreatedOn());
        dto.setModifiedOn(recurso.getModifiedOn());
        return dto;
    }
}
