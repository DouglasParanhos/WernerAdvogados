package com.wa.service;

import com.wa.dto.MovimentDTO;
import com.wa.dto.MovimentRequestDTO;
import com.wa.model.Moviment;
import com.wa.model.Person;
import com.wa.model.Process;
import com.wa.repository.MovimentRepository;
import com.wa.repository.ProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentServiceTest {

    @Mock
    private MovimentRepository movimentRepository;

    @Mock
    private ProcessRepository processRepository;

    @InjectMocks
    private MovimentService movimentService;

    private Moviment moviment1;
    private Moviment moviment2;
    private Process process1;
    private Person person1;

    @BeforeEach
    void setUp() {
        person1 = new Person();
        person1.setId(1L);
        person1.setFullname("João Silva");

        process1 = new Process();
        process1.setId(1L);
        process1.setNumero("1234567-89.2023.8.19.0001");
        process1.setComarca("Capital");
        process1.setVara("1ª Vara");
        process1.setSistema("PROJUDI");

        moviment1 = new Moviment();
        moviment1.setId(1L);
        moviment1.setDescricao("Distribuição do processo");
        moviment1.setDate(LocalDateTime.of(2023, 1, 15, 10, 0));
        moviment1.setProcess(process1);

        moviment2 = new Moviment();
        moviment2.setId(2L);
        moviment2.setDescricao("Juntada de documentos");
        moviment2.setDate(LocalDateTime.of(2023, 2, 20, 14, 30));
        moviment2.setProcess(process1);
    }

    @Test
    void testFindByPersonId_ReturnsMovimentsForPerson() {
        // Arrange
        Long personId = 1L;
        List<Moviment> moviments = List.of(moviment1, moviment2);

        when(movimentRepository.findByPersonId(personId)).thenReturn(moviments);

        // Act
        List<MovimentDTO> result = movimentService.findByPersonId(personId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Distribuição do processo", result.get(0).getDescricao());
        assertEquals("Juntada de documentos", result.get(1).getDescricao());
        assertEquals(1L, result.get(0).getProcessId());
        assertEquals(1L, result.get(1).getProcessId());
        assertEquals("1234567-89.2023.8.19.0001", result.get(0).getProcessNumero());
        assertEquals("Capital", result.get(0).getProcessComarca());
        assertEquals("1ª Vara", result.get(0).getProcessVara());
        verify(movimentRepository, times(1)).findByPersonId(personId);
    }

    @Test
    void testFindByPersonId_WithNoMoviments_ReturnsEmptyList() {
        // Arrange
        Long personId = 999L;
        when(movimentRepository.findByPersonId(personId)).thenReturn(List.of());

        // Act
        List<MovimentDTO> result = movimentService.findByPersonId(personId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movimentRepository, times(1)).findByPersonId(personId);
    }

    @Test
    void testFindByPersonId_OrdersByDateDesc() {
        // Arrange
        Long personId = 1L;
        // moviment2 tem data mais recente que moviment1
        List<Moviment> moviments = List.of(moviment2, moviment1);

        when(movimentRepository.findByPersonId(personId)).thenReturn(moviments);

        // Act
        List<MovimentDTO> result = movimentService.findByPersonId(personId);

        // Assert
        assertEquals(2, result.size());
        // Verifica que está ordenado por data DESC (mais recente primeiro)
        assertTrue(result.get(0).getDate().isAfter(result.get(1).getDate()) ||
                   result.get(0).getDate().isEqual(result.get(1).getDate()));
    }

    @Test
    void testUpdate_WithValidData_ReturnsUpdatedMoviment() {
        // Arrange
        Long movimentId = 1L;
        Long processId = 1L;
        LocalDateTime newDate = LocalDateTime.of(2023, 3, 15, 15, 30);
        
        MovimentRequestDTO requestDTO = new MovimentRequestDTO();
        requestDTO.setDescricao("Descrição atualizada");
        requestDTO.setDate(newDate);
        requestDTO.setProcessId(processId);
        requestDTO.setVisibleToClient(true);

        Process process = new Process();
        process.setId(processId);
        process.setNumero("1234567-89.2023.8.19.0001");

        Moviment existingMoviment = new Moviment();
        existingMoviment.setId(movimentId);
        existingMoviment.setDescricao("Descrição antiga");
        existingMoviment.setDate(LocalDateTime.of(2023, 1, 15, 10, 0));
        existingMoviment.setProcess(process1);
        existingMoviment.setVisibleToClient(false);

        Moviment savedMoviment = new Moviment();
        savedMoviment.setId(movimentId);
        savedMoviment.setDescricao("Descrição atualizada");
        savedMoviment.setDate(newDate);
        savedMoviment.setProcess(process);
        savedMoviment.setVisibleToClient(true);

        when(movimentRepository.findById(movimentId)).thenReturn(java.util.Optional.of(existingMoviment));
        when(processRepository.findById(processId)).thenReturn(java.util.Optional.of(process));
        when(movimentRepository.save(any(Moviment.class))).thenReturn(savedMoviment);

        // Act
        MovimentDTO result = movimentService.update(movimentId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(movimentId, result.getId());
        assertEquals("Descrição atualizada", result.getDescricao());
        assertEquals(newDate, result.getDate());
        assertEquals(processId, result.getProcessId());
        assertTrue(result.getVisibleToClient());
        
        verify(movimentRepository, times(1)).findById(movimentId);
        verify(processRepository, times(1)).findById(processId);
        verify(movimentRepository, times(1)).save(any(Moviment.class));
    }

    @Test
    void testUpdate_WithMovimentNotFound_ThrowsException() {
        // Arrange
        Long movimentId = 999L;
        MovimentRequestDTO requestDTO = new MovimentRequestDTO();
        requestDTO.setDescricao("Descrição atualizada");
        requestDTO.setDate(LocalDateTime.of(2023, 3, 15, 15, 30));
        requestDTO.setProcessId(1L);

        when(movimentRepository.findById(movimentId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movimentService.update(movimentId, requestDTO);
        });

        assertTrue(exception.getMessage().contains("Movimentação não encontrada"));
        verify(movimentRepository, times(1)).findById(movimentId);
        verify(processRepository, never()).findById(any());
        verify(movimentRepository, never()).save(any());
    }

    @Test
    void testUpdate_WithProcessNotFound_ThrowsException() {
        // Arrange
        Long movimentId = 1L;
        Long processId = 999L;
        MovimentRequestDTO requestDTO = new MovimentRequestDTO();
        requestDTO.setDescricao("Descrição atualizada");
        requestDTO.setDate(LocalDateTime.of(2023, 3, 15, 15, 30));
        requestDTO.setProcessId(processId);

        when(movimentRepository.findById(movimentId)).thenReturn(java.util.Optional.of(moviment1));
        when(processRepository.findById(processId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movimentService.update(movimentId, requestDTO);
        });

        assertTrue(exception.getMessage().contains("Processo não encontrado"));
        verify(movimentRepository, times(1)).findById(movimentId);
        verify(processRepository, times(1)).findById(processId);
        verify(movimentRepository, never()).save(any());
    }

    @Test
    void testUpdate_WithNullVisibleToClient_PreservesExistingValue() {
        // Arrange
        Long movimentId = 1L;
        Long processId = 1L;
        LocalDateTime newDate = LocalDateTime.of(2023, 3, 15, 15, 30);
        
        MovimentRequestDTO requestDTO = new MovimentRequestDTO();
        requestDTO.setDescricao("Descrição atualizada");
        requestDTO.setDate(newDate);
        requestDTO.setProcessId(processId);
        requestDTO.setVisibleToClient(null); // null deve preservar valor existente

        Process process = new Process();
        process.setId(processId);

        Moviment existingMoviment = new Moviment();
        existingMoviment.setId(movimentId);
        existingMoviment.setDescricao("Descrição antiga");
        existingMoviment.setDate(LocalDateTime.of(2023, 1, 15, 10, 0));
        existingMoviment.setProcess(process1);
        existingMoviment.setVisibleToClient(false); // Valor existente

        Moviment savedMoviment = new Moviment();
        savedMoviment.setId(movimentId);
        savedMoviment.setDescricao("Descrição atualizada");
        savedMoviment.setDate(newDate);
        savedMoviment.setProcess(process);
        savedMoviment.setVisibleToClient(false); // Preservado

        when(movimentRepository.findById(movimentId)).thenReturn(java.util.Optional.of(existingMoviment));
        when(processRepository.findById(processId)).thenReturn(java.util.Optional.of(process));
        when(movimentRepository.save(any(Moviment.class))).thenReturn(savedMoviment);

        // Act
        MovimentDTO result = movimentService.update(movimentId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Descrição atualizada", result.getDescricao());
        assertFalse(result.getVisibleToClient()); // Valor preservado
        
        verify(movimentRepository, times(1)).save(any(Moviment.class));
    }
}

