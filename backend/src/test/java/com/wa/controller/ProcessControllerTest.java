package com.wa.controller;

import com.wa.dto.ProcessDTO;
import com.wa.service.ProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProcessController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessService processService;

    private ProcessDTO processDTO1;
    private ProcessDTO processDTO2;
    private ProcessDTO processDTO3;

    @BeforeEach
    void setUp() {
        processDTO1 = new ProcessDTO();
        processDTO1.setId(1L);
        processDTO1.setNumero("0012345-67.2023.8.19.0001");
        processDTO1.setComarca("Rio de Janeiro");
        processDTO1.setVara("1ª Vara");
        processDTO1.setTipoProcesso("PISO");
        processDTO1.setStatus("Em andamento");

        processDTO2 = new ProcessDTO();
        processDTO2.setId(2L);
        processDTO2.setNumero("0023456-78.2023.8.19.0001");
        processDTO2.setComarca("Niterói");
        processDTO2.setVara("2ª Vara");
        processDTO2.setTipoProcesso("NOVAESCOLA");
        processDTO2.setStatus("Concluído");

        processDTO3 = new ProcessDTO();
        processDTO3.setId(3L);
        processDTO3.setNumero("0034567-89.2023.8.19.0001");
        processDTO3.setComarca("Rio de Janeiro");
        processDTO3.setVara("3ª Vara");
        processDTO3.setTipoProcesso("INTERNIVEIS");
        processDTO3.setStatus("Arquivado");
    }

    @Test
    void testFindAll_WithPagination_ReturnsPaginatedResponse() throws Exception {
        // Arrange
        List<ProcessDTO> processes = List.of(processDTO1, processDTO2, processDTO3);
        Page<ProcessDTO> page = new PageImpl<>(processes, PageRequest.of(0, 10), 3);

        when(processService.findAllPaginated(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(processService, times(1)).findAllPaginated(
                null, null, null, null, null, false, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithNumeroFilter_ReturnsFilteredResults() throws Exception {
        // Arrange
        String numero = "0012345";
        List<ProcessDTO> filteredProcesses = List.of(processDTO1);
        Page<ProcessDTO> page = new PageImpl<>(filteredProcesses, PageRequest.of(0, 10), 1);

        when(processService.findAllPaginated(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("numero", numero)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].numero").value("0012345-67.2023.8.19.0001"));

        verify(processService, times(1)).findAllPaginated(
                numero, null, null, null, null, false, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithComarcaFilter_ReturnsFilteredResults() throws Exception {
        // Arrange
        String comarca = "Rio de Janeiro";
        List<ProcessDTO> filteredProcesses = List.of(processDTO1, processDTO3);
        Page<ProcessDTO> page = new PageImpl<>(filteredProcesses, PageRequest.of(0, 10), 2);

        when(processService.findAllPaginated(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("comarca", comarca)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(processService, times(1)).findAllPaginated(
                null, comarca, null, null, null, false, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithStatusFilter_ReturnsFilteredResults() throws Exception {
        // Arrange
        String status = "Em andamento";
        List<ProcessDTO> filteredProcesses = List.of(processDTO1);
        Page<ProcessDTO> page = new PageImpl<>(filteredProcesses, PageRequest.of(0, 10), 1);

        when(processService.findAllPaginated(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("Em andamento"));

        verify(processService, times(1)).findAllPaginated(
                null, null, null, null, status, false, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithShowArchived_ReturnsArchivedProcesses() throws Exception {
        // Arrange
        List<ProcessDTO> allProcesses = List.of(processDTO1, processDTO2, processDTO3);
        Page<ProcessDTO> page = new PageImpl<>(allProcesses, PageRequest.of(0, 10), 3);

        when(processService.findAllPaginated(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("showArchived", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3));

        verify(processService, times(1)).findAllPaginated(
                null, null, null, null, null, true, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithMultipleFilters_ReturnsFilteredResults() throws Exception {
        // Arrange
        String comarca = "Rio de Janeiro";
        String tipoProcesso = "PISO";
        List<ProcessDTO> filteredProcesses = List.of(processDTO1);
        Page<ProcessDTO> page = new PageImpl<>(filteredProcesses, PageRequest.of(0, 10), 1);

        when(processService.findAllPaginated(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("comarca", comarca)
                        .param("tipoProcesso", tipoProcesso)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].comarca").value("Rio de Janeiro"))
                .andExpect(jsonPath("$.content[0].tipoProcesso").value("PISO"));

        verify(processService, times(1)).findAllPaginated(
                null, comarca, null, tipoProcesso, null, false, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithDifferentPageSizes() throws Exception {
        // Arrange
        List<ProcessDTO> processes = List.of(processDTO1, processDTO2, processDTO3);
        Page<ProcessDTO> page10 = new PageImpl<>(processes, PageRequest.of(0, 10), 3);
        Page<ProcessDTO> page50 = new PageImpl<>(processes, PageRequest.of(0, 50), 3);
        Page<ProcessDTO> page100 = new PageImpl<>(processes, PageRequest.of(0, 100), 3);

        when(processService.findAllPaginated(null, null, null, null, null, false, PageRequest.of(0, 10)))
                .thenReturn(page10);
        when(processService.findAllPaginated(null, null, null, null, null, false, PageRequest.of(0, 50)))
                .thenReturn(page50);
        when(processService.findAllPaginated(null, null, null, null, null, false, PageRequest.of(0, 100)))
                .thenReturn(page100);

        // Act & Assert - Size 10
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10));

        // Act & Assert - Size 50
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));

        // Act & Assert - Size 100
        mockMvc.perform(get("/api/processes")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100));
    }

    @Test
    void testFindAll_WithoutPaginationParams_ReturnsList() throws Exception {
        // Arrange
        List<ProcessDTO> processes = List.of(processDTO1, processDTO2, processDTO3);
        when(processService.findAll()).thenReturn(processes);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(processService, times(1)).findAll();
        verify(processService, never()).findAllPaginated(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testFindAll_WithPersonId_ReturnsPersonProcesses() throws Exception {
        // Arrange
        Long personId = 1L;
        List<ProcessDTO> processes = List.of(processDTO1, processDTO2);
        when(processService.findByPersonId(personId)).thenReturn(processes);

        // Act & Assert
        mockMvc.perform(get("/api/processes")
                        .param("personId", String.valueOf(personId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(processService, times(1)).findByPersonId(personId);
        verify(processService, never()).findAllPaginated(any(), any(), any(), any(), any(), any(), any());
    }
}

