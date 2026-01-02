package com.wa.service;

import com.wa.dto.ProcessDTO;
import com.wa.model.Matriculation;
import com.wa.model.Person;
import com.wa.model.Process;
import com.wa.repository.MatriculationRepository;
import com.wa.repository.ProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private MatriculationRepository matriculationRepository;

    @InjectMocks
    private ProcessService processService;

    private Process process1;
    private Process process2;
    private Process process3;
    private Matriculation matriculation;
    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");

        matriculation = new Matriculation();
        matriculation.setId(1L);
        matriculation.setPerson(person);

        process1 = new Process();
        process1.setId(1L);
        process1.setNumero("0012345-67.2023.8.19.0001");
        process1.setComarca("Rio de Janeiro");
        process1.setVara("1ª Vara");
        process1.setTipoProcesso("PISO");
        process1.setStatus("Em andamento");
        process1.setMatriculation(matriculation);

        process2 = new Process();
        process2.setId(2L);
        process2.setNumero("0023456-78.2023.8.19.0001");
        process2.setComarca("Niterói");
        process2.setVara("2ª Vara");
        process2.setTipoProcesso("NOVAESCOLA");
        process2.setStatus("Concluído");
        process2.setMatriculation(matriculation);

        process3 = new Process();
        process3.setId(3L);
        process3.setNumero("0034567-89.2023.8.19.0001");
        process3.setComarca("Rio de Janeiro");
        process3.setVara("3ª Vara");
        process3.setTipoProcesso("INTERNIVEIS");
        process3.setStatus("Arquivado");
        process3.setMatriculation(matriculation);
    }

    @Test
    void testFindAllPaginated_WithoutFilters_ReturnsAllResults() {
        // Arrange
        List<Process> processes = List.of(process1, process2, process3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Process> page = new PageImpl<>(processes, pageable, 3);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<ProcessDTO> result = processService.findAllPaginated(
                null, null, null, null, null, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        verify(processRepository, times(1)).findAllWithRelationsPaginated(
                null, null, null, null, null, false, pageable);
    }

    @Test
    void testFindAllPaginated_WithNumeroFilter_ReturnsFilteredResults() {
        // Arrange
        String numero = "0012345";
        List<Process> filteredProcesses = List.of(process1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Process> page = new PageImpl<>(filteredProcesses, pageable, 1);

        when(processRepository.findAllWithRelationsPaginated(
                eq(numero), isNull(), isNull(), isNull(), isNull(), eq(false), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<ProcessDTO> result = processService.findAllPaginated(
                numero, null, null, null, null, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("0012345-67.2023.8.19.0001", result.getContent().get(0).getNumero());
    }

    @Test
    void testFindAllPaginated_WithComarcaFilter_ReturnsFilteredResults() {
        // Arrange
        String comarca = "Rio de Janeiro";
        List<Process> filteredProcesses = List.of(process1, process3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Process> page = new PageImpl<>(filteredProcesses, pageable, 2);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), eq(comarca), isNull(), isNull(), isNull(), eq(false), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<ProcessDTO> result = processService.findAllPaginated(
                null, comarca, null, null, null, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testFindAllPaginated_WithStatusFilter_ReturnsFilteredResults() {
        // Arrange
        String status = "Em andamento";
        List<Process> filteredProcesses = List.of(process1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Process> page = new PageImpl<>(filteredProcesses, pageable, 1);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), eq(status), eq(false), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<ProcessDTO> result = processService.findAllPaginated(
                null, null, null, null, status, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Em andamento", result.getContent().get(0).getStatus());
    }

    @Test
    void testFindAllPaginated_WithShowArchived_ReturnsArchivedProcesses() {
        // Arrange
        List<Process> allProcesses = List.of(process1, process2, process3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Process> page = new PageImpl<>(allProcesses, pageable, 3);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<ProcessDTO> result = processService.findAllPaginated(
                null, null, null, null, null, true, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        verify(processRepository, times(1)).findAllWithRelationsPaginated(
                null, null, null, null, null, true, pageable);
    }

    @Test
    void testFindAllPaginated_WithMultipleFilters_ReturnsFilteredResults() {
        // Arrange
        String comarca = "Rio de Janeiro";
        String tipoProcesso = "PISO";
        List<Process> filteredProcesses = List.of(process1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Process> page = new PageImpl<>(filteredProcesses, pageable, 1);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), eq(comarca), isNull(), eq(tipoProcesso), isNull(), eq(false), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<ProcessDTO> result = processService.findAllPaginated(
                null, comarca, null, tipoProcesso, null, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Rio de Janeiro", result.getContent().get(0).getComarca());
        assertEquals("PISO", result.getContent().get(0).getTipoProcesso());
    }

    @Test
    void testFindAllPaginated_WithPagination_ReturnsCorrectPage() {
        // Arrange
        List<Process> page1Processes = List.of(process1, process2);
        List<Process> page2Processes = List.of(process3);
        Pageable pageable1 = PageRequest.of(0, 2);
        Pageable pageable2 = PageRequest.of(1, 2);
        Page<Process> page1 = new PageImpl<>(page1Processes, pageable1, 3);
        Page<Process> page2 = new PageImpl<>(page2Processes, pageable2, 3);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), eq(pageable1)))
                .thenReturn(page1);
        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), eq(pageable2)))
                .thenReturn(page2);

        // Act
        Page<ProcessDTO> result1 = processService.findAllPaginated(
                null, null, null, null, null, false, pageable1);
        Page<ProcessDTO> result2 = processService.findAllPaginated(
                null, null, null, null, null, false, pageable2);

        // Assert
        assertEquals(2, result1.getContent().size());
        assertEquals(1, result2.getContent().size());
        assertEquals(3, result1.getTotalElements());
        assertEquals(3, result2.getTotalElements());
    }

    @Test
    void testFindAllPaginated_WithDifferentPageSizes() {
        // Arrange
        List<Process> processes = List.of(process1, process2, process3);
        Pageable pageable10 = PageRequest.of(0, 10);
        Pageable pageable50 = PageRequest.of(0, 50);
        Pageable pageable100 = PageRequest.of(0, 100);
        Page<Process> page10 = new PageImpl<>(processes, pageable10, 3);
        Page<Process> page50 = new PageImpl<>(processes, pageable50, 3);
        Page<Process> page100 = new PageImpl<>(processes, pageable100, 3);

        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), eq(pageable10)))
                .thenReturn(page10);
        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), eq(pageable50)))
                .thenReturn(page50);
        when(processRepository.findAllWithRelationsPaginated(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), eq(pageable100)))
                .thenReturn(page100);

        // Act
        Page<ProcessDTO> result10 = processService.findAllPaginated(
                null, null, null, null, null, false, pageable10);
        Page<ProcessDTO> result50 = processService.findAllPaginated(
                null, null, null, null, null, false, pageable50);
        Page<ProcessDTO> result100 = processService.findAllPaginated(
                null, null, null, null, null, false, pageable100);

        // Assert
        assertEquals(10, result10.getSize());
        assertEquals(50, result50.getSize());
        assertEquals(100, result100.getSize());
        assertEquals(3, result10.getTotalElements());
        assertEquals(3, result50.getTotalElements());
        assertEquals(3, result100.getTotalElements());
    }
}

