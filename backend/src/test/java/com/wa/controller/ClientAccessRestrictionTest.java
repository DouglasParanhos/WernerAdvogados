package com.wa.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.wa.service.PersonService;
import com.wa.service.ProcessService;
import com.wa.service.TaskService;
import com.wa.service.StatisticsService;
import com.wa.service.ProcessValueCorrectionService;
import com.wa.service.ProcessUpdateStatusService;
import com.wa.service.NovaEscolaCalculationService;
import com.wa.service.ExcelGenerationService;
import com.wa.service.BcbCalculatorScraper;
import com.wa.service.MatriculationService;
import com.wa.service.MovimentService;
import com.wa.service.DocumentTemplateService;
import com.wa.service.WordDocumentService;
import com.wa.service.BackupService;
import com.wa.repository.PersonRepository;
import com.wa.repository.UserRepository;
import com.wa.repository.ProcessRepository;
import com.wa.util.JWTUtil;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes para verificar que usuários CLIENT são bloqueados de acessar endpoints
 * que possuem a anotação @RequiresNonClient
 * 
 * Nota: Estes testes verificam o comportamento das anotações @PreAuthorize
 * através do Spring Security Method Security. Os testes mockam o SecurityContext
 * para simular um usuário CLIENT autenticado.
 */
@WebMvcTest(controllers = {
    PersonController.class,
    ProcessController.class,
    TaskController.class,
    StatisticsController.class,
    CalculationController.class,
    MatriculationController.class,
    MovimentController.class,
    DocumentController.class,
    BackupController.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class ClientAccessRestrictionTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock all required services
    @MockBean
    private PersonService personService;
    @MockBean
    private ProcessService processService;
    @MockBean
    private TaskService taskService;
    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private ProcessValueCorrectionService processValueCorrectionService;
    @MockBean
    private ProcessUpdateStatusService processUpdateStatusService;
    @MockBean
    private NovaEscolaCalculationService novaEscolaCalculationService;
    @MockBean
    private ExcelGenerationService excelGenerationService;
    @MockBean
    private BcbCalculatorScraper bcbCalculatorScraper;
    @MockBean
    private MatriculationService matriculationService;
    @MockBean
    private MovimentService movimentService;
    @MockBean
    private DocumentTemplateService documentTemplateService;
    @MockBean
    private WordDocumentService wordDocumentService;
    @MockBean
    private BackupService backupService;
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProcessRepository processRepository;
    @MockBean
    private JWTUtil jwtUtil;

    private Authentication clientAuthentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        // Setup CLIENT authentication
        clientAuthentication = mock(Authentication.class);
        when(clientAuthentication.isAuthenticated()).thenReturn(true);
        when(clientAuthentication.getName()).thenReturn("client.user");
        when(clientAuthentication.getAuthorities()).thenAnswer(invocation ->
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(clientAuthentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // PersonController Tests
    @Test
    void testPersonController_FindAll_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPersonController_FindById_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/persons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPersonController_Create_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "fullname": "Test User",
                    "email": "test@example.com",
                    "cpf": "12345678900"
                }
                """;

        mockMvc.perform(post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPersonController_Update_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "fullname": "Updated User",
                    "email": "updated@example.com"
                }
                """;

        mockMvc.perform(put("/api/v1/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPersonController_Delete_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/persons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPersonController_GetUsernameSuggestion_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/persons/1/username-suggestion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // ProcessController Tests
    @Test
    void testProcessController_FindAll_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/processes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProcessController_FindById_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/processes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProcessController_Create_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "numero": "1234567-89.2023.8.19.0001",
                    "comarca": "Capital",
                    "vara": "1ª Vara"
                }
                """;

        mockMvc.perform(post("/api/v1/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProcessController_Update_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "numero": "1234567-89.2023.8.19.0001",
                    "comarca": "Updated Comarca"
                }
                """;

        mockMvc.perform(put("/api/v1/processes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProcessController_Delete_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/processes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProcessController_GetDistinctStatuses_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/processes/status/distinct")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProcessController_UpdateStatus_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "status": "EM_ANDAMENTO"
                }
                """;

        mockMvc.perform(patch("/api/v1/processes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // TaskController Tests
    @Test
    void testTaskController_GetAll_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTaskController_GetById_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTaskController_Create_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "descricao": "Nova tarefa",
                    "tipoTarefa": "PRESENCIAL"
                }
                """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTaskController_Update_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "descricao": "Tarefa atualizada"
                }
                """;

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTaskController_Delete_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTaskController_UpdateOrder_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                [
                    {
                        "id": 1,
                        "ordem": 1
                    }
                ]
                """;

        mockMvc.perform(put("/api/v1/tasks/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // StatisticsController Tests
    @Test
    void testStatisticsController_GetStatistics_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testStatisticsController_UpdateProcessValues_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/statistics/update-process-values")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testStatisticsController_GetProcessStatus_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/process-status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // CalculationController Tests
    @Test
    void testCalculationController_CalculateNovaEscola_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "baseValue": 1000.0,
                    "ipcaEFactor": 2.88680560,
                    "selicFactor": 1.5
                }
                """;

        mockMvc.perform(post("/api/v1/calculations/novaescola")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCalculationController_GetIpcaEFactor_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/calculations/factors/ipcae")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCalculationController_GetSelicFactor_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/calculations/factors/selic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // MatriculationController Tests
    @Test
    void testMatriculationController_FindAll_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/matriculations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMatriculationController_FindById_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/matriculations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMatriculationController_Create_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "numero": "123456",
                    "personId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/matriculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMatriculationController_Update_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "numero": "654321"
                }
                """;

        mockMvc.perform(put("/api/v1/matriculations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMatriculationController_Delete_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/matriculations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // MovimentController Tests (except getMyMoviments)
    @Test
    void testMovimentController_FindAll_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/moviments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMovimentController_FindById_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/moviments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMovimentController_Create_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "descricao": "Nova movimentação",
                    "processId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/moviments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMovimentController_Update_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "descricao": "Movimentação atualizada"
                }
                """;

        mockMvc.perform(put("/api/v1/moviments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMovimentController_Delete_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/moviments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // DocumentController Tests
    @Test
    void testDocumentController_GetTemplates_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/documents/templates")
                        .param("processId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDocumentController_GenerateDocument_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                    "processId": 1,
                    "templateName": "template.docx"
                }
                """;

        mockMvc.perform(post("/api/v1/documents/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // BackupController Tests
    @Test
    void testBackupController_CreateBackup_WithClientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/backup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
