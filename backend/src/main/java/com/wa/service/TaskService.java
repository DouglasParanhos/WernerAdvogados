package com.wa.service;

import com.wa.dto.TaskDTO;
import com.wa.dto.TaskRequestDTO;
import com.wa.model.Task;
import com.wa.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
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
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getResponsavel() != null) task.setResponsavel(request.getResponsavel());
        if (request.getOrdem() != null) task.setOrdem(request.getOrdem());
        
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
        dto.setOrdem(task.getOrdem());
        dto.setCreatedOn(task.getCreatedOn());
        dto.setModifiedOn(task.getModifiedOn());
        return dto;
    }
}

