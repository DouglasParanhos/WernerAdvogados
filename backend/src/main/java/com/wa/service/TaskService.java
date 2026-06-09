package com.wa.service;

import com.wa.dto.TaskDTO;
import com.wa.dto.TaskRequestDTO;
import com.wa.model.Task;
import com.wa.repository.ProcessRepository;
import com.wa.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProcessRepository processRepository;
    
    public List<TaskDTO> getAll() {
        return taskRepository.findAllByOrderByStatusAscOrdemAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public TaskDTO getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        return convertToDTO(task);
    }
    
    @Transactional
    public TaskDTO create(TaskRequestDTO request) {
        Task task = new Task();
        task.setTitulo(request.getTitulo());
        task.setDescricao(request.getDescricao());
        task.setTipoTarefa(request.getTipoTarefa());
        task.setStatus(request.getStatus() != null ? request.getStatus() : "PARA_INICIAR");
        task.setResponsavel(request.getResponsavel());
        resolveProcessId(task, request.getProcessId());
        
        // Se não especificada, definir ordem como última da coluna
        if (request.getOrdem() == null) {
            List<Task> tasksInStatus = taskRepository.findByStatusOrderByOrdemAsc(task.getStatus());
            int maxOrdem = tasksInStatus.stream()
                    .mapToInt(t -> t.getOrdem() != null ? t.getOrdem() : 0)
                    .max()
                    .orElse(0);
            task.setOrdem(maxOrdem + 1);
        } else {
            task.setOrdem(request.getOrdem());
        }
        
        task.setPrazoFinal(request.getPrazoFinal());
        validatePrazoFinalForTipo(task.getTipoTarefa(), task.getPrazoFinal());
        
        Task saved = taskRepository.save(task);
        return convertToDTO(saved);
    }
    
    @Transactional
    public TaskDTO update(Long id, TaskRequestDTO request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        if (request.getTitulo() != null) task.setTitulo(request.getTitulo());
        if (request.getDescricao() != null) task.setDescricao(request.getDescricao());
        if (request.getTipoTarefa() != null) task.setTipoTarefa(request.getTipoTarefa());
        if (request.getStatus() != null) {
            String newStatus = request.getStatus();
            String oldStatus = task.getStatus();
            task.setStatus(newStatus);
            if ("COMPLETA".equals(newStatus) && !"COMPLETA".equals(oldStatus)) {
                task.setCompletedOn(LocalDateTime.now());
            } else if (!"COMPLETA".equals(newStatus) && "COMPLETA".equals(oldStatus)) {
                task.setCompletedOn(null);
            }
        }
        if (request.getResponsavel() != null) task.setResponsavel(request.getResponsavel());
        if (request.getOrdem() != null) task.setOrdem(request.getOrdem());
        resolveProcessId(task, request.getProcessId());
        task.setPrazoFinal(request.getPrazoFinal());
        
        validatePrazoFinalForTipo(task.getTipoTarefa(), task.getPrazoFinal());
        
        Task saved = taskRepository.save(task);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Tarefa não encontrada");
        }
        taskRepository.deleteById(id);
    }
    
    @Transactional
    public List<TaskDTO> updateOrder(List<TaskRequestDTO> tasks) {
        for (TaskRequestDTO taskDTO : tasks) {
            if (taskDTO.getId() == null) {
                continue;
            }
            Task task = taskRepository.findById(taskDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + taskDTO.getId()));
            if (taskDTO.getStatus() != null) {
                task.setStatus(taskDTO.getStatus());
            }
            if (taskDTO.getOrdem() != null) {
                task.setOrdem(taskDTO.getOrdem());
            }
            taskRepository.save(task);
        }
        return getAll();
    }
    
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitulo(task.getTitulo());
        dto.setDescricao(task.getDescricao());
        dto.setTipoTarefa(task.getTipoTarefa());
        dto.setStatus(task.getStatus());
        dto.setResponsavel(task.getResponsavel());
        dto.setProcessId(task.getProcessId());
        if (task.getProcessId() != null) {
            processRepository.findById(task.getProcessId())
                    .ifPresent(p -> dto.setProcessNumero(p.getNumero()));
        } else {
            dto.setProcessNumero(null);
        }
        dto.setPrazoFinal(task.getPrazoFinal());
        dto.setOrdem(task.getOrdem());
        dto.setCompletedOn(task.getCompletedOn());
        dto.setCreatedOn(task.getCreatedOn());
        dto.setModifiedOn(task.getModifiedOn());
        return dto;
    }

    private void resolveProcessId(Task task, Long processId) {
        if (processId != null && !processRepository.existsById(processId)) {
            throw new RuntimeException("Processo não encontrado");
        }
        task.setProcessId(processId);
    }

    private void validatePrazoFinalForTipo(String tipoTarefa, LocalDate prazoFinal) {
        if ("PRAZO".equals(tipoTarefa) && prazoFinal == null) {
            throw new IllegalArgumentException("Prazo final é obrigatório para tarefas do tipo Prazo.");
        }
    }
}

