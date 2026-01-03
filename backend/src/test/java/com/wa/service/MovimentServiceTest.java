package com.wa.service;

import com.wa.dto.MovimentDTO;
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
}

