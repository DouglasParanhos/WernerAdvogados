package com.wa.service;

import com.wa.dto.TaskDTO;
import com.wa.dto.TaskRequestDTO;
import com.wa.exception.TaskNotFoundException;
import com.wa.model.Task;
import com.wa.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDTO taskRequest;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitulo("Test Task");
        task.setDescricao("Test Description");
        task.setTipoTarefa("TIPO1");
        task.setStatus("PARA_INICIAR");
        task.setResponsavel("John Doe");
        task.setOrdem(1);
        task.setCreatedOn(LocalDateTime.now());
        task.setModifiedOn(LocalDateTime.now());

        taskRequest = new TaskRequestDTO();
        taskRequest.setTitulo("New Task");
        taskRequest.setDescricao("New Description");
        taskRequest.setTipoTarefa("TIPO1");
        taskRequest.setStatus("EM_ANDAMENTO");
        taskRequest.setResponsavel("Jane Doe");
        taskRequest.setOrdem(2);
    }

    @Test
    void testGetById_Success_ReturnsTaskDTO() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        TaskDTO result = taskService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitulo());
        assertEquals("Test Description", result.getDescricao());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_TaskNotFound_ThrowsTaskNotFoundException() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getById(999L);
        });

        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdate_Success_ReturnsUpdatedTaskDTO() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDTO result = taskService.update(1L, taskRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdate_TaskNotFound_ThrowsTaskNotFoundException() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.update(999L, taskRequest);
        });

        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDelete_Success_DeletesTask() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);

        // Act
        taskService.delete(1L);

        // Assert
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_TaskNotFound_ThrowsTaskNotFoundException() {
        // Arrange
        when(taskRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.delete(999L);
        });

        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateOrder_TaskNotFound_ThrowsTaskNotFoundException() {
        // Arrange
        TaskRequestDTO taskDTO = new TaskRequestDTO();
        taskDTO.setId(999L);
        taskDTO.setStatus("CONCLUIDA");
        taskDTO.setOrdem(1);

        List<TaskRequestDTO> tasks = List.of(taskDTO);
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateOrder(tasks);
        });

        verify(taskRepository, times(1)).findById(999L);
    }
}

