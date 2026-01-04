package com.wa.service;

import com.wa.dto.MovimentDTO;
import com.wa.dto.MovimentRequestDTO;
import com.wa.model.Moviment;
import com.wa.model.Process;
import com.wa.repository.MovimentRepository;
import com.wa.repository.ProcessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimentService {

    private final MovimentRepository movimentRepository;
    private final ProcessRepository processRepository;

    public List<MovimentDTO> findAll() {
        return movimentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovimentDTO> findByProcessId(Long processId) {
        return movimentRepository.findByProcessId(processId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovimentDTO> findByPersonId(Long personId) {
        return movimentRepository.findByPersonId(personId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MovimentDTO findById(Long id) {
        Moviment moviment = movimentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada com ID: " + id));
        return convertToDTO(moviment);
    }

    @Transactional
    public MovimentDTO create(MovimentRequestDTO request) {
        Process process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + request.getProcessId()));

        Moviment moviment = new Moviment();
        moviment.setDescricao(request.getDescricao());
        moviment.setDate(request.getDate());
        moviment.setProcess(process);
        moviment.setVisibleToClient(request.getVisibleToClient() != null ? request.getVisibleToClient() : true);

        moviment = movimentRepository.save(moviment);
        return convertToDTO(moviment);
    }

    @Transactional
    public MovimentDTO update(Long id, MovimentRequestDTO request) {
        Moviment moviment = movimentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada com ID: " + id));

        Process process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + request.getProcessId()));

        moviment.setDescricao(request.getDescricao());
        moviment.setDate(request.getDate());
        moviment.setProcess(process);
        if (request.getVisibleToClient() != null) {
            moviment.setVisibleToClient(request.getVisibleToClient());
        }

        moviment = movimentRepository.save(moviment);
        return convertToDTO(moviment);
    }

    @Transactional
    public void delete(Long id) {
        Moviment moviment = movimentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada com ID: " + id));
        movimentRepository.delete(moviment);
    }

    private MovimentDTO convertToDTO(Moviment moviment) {
        MovimentDTO dto = new MovimentDTO();
        dto.setId(moviment.getId());
        dto.setDescricao(moviment.getDescricao());
        dto.setDate(moviment.getDate());
        dto.setProcessId(moviment.getProcess().getId());
        dto.setVisibleToClient(moviment.getVisibleToClient());
        // Incluir informações do processo se disponível
        if (moviment.getProcess() != null) {
            dto.setProcessNumero(moviment.getProcess().getNumero());
            dto.setProcessComarca(moviment.getProcess().getComarca());
            dto.setProcessVara(moviment.getProcess().getVara());
            dto.setProcessTipoProcesso(moviment.getProcess().getTipoProcesso());
            dto.setProcessStatus(moviment.getProcess().getStatus());
            // Incluir informações da matrícula se disponível
            if (moviment.getProcess().getMatriculation() != null) {
                dto.setProcessMatriculationNumero(moviment.getProcess().getMatriculation().getNumero());
            }
        }
        return dto;
    }
}
