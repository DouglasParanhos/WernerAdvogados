package com.wa.controller;

import com.wa.model.Person;
import com.wa.model.User;
import com.wa.repository.PersonRepository;
import com.wa.repository.ProcessRepository;
import com.wa.repository.UserRepository;
import com.wa.service.DocumentTemplateService;
import com.wa.service.WordDocumentService;
import com.wa.util.JWTUtil;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de segurança para endpoints de documentos
 * Testa proteção contra XSS, path traversal, DoS e injeção de código
 */
@WebMvcTest(controllers = DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class DocumentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentTemplateService templateService;

    @MockBean
    private WordDocumentService wordDocumentService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private ProcessRepository processRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JWTUtil jwtUtil;

    private User adminUser;
    private Person testPerson;
    private Authentication adminAuthentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin.user");
        adminUser.setRole("ADMIN");

        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setFullname("Test Person");
        testPerson.setUser(adminUser);

        // Setup ADMIN authentication
        adminAuthentication = mock(Authentication.class);
        when(adminAuthentication.isAuthenticated()).thenReturn(true);
        when(adminAuthentication.getName()).thenReturn("admin.user");
        when(adminAuthentication.getAuthorities()).thenAnswer(invocation ->
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(adminAuthentication);
    }

    // ========== Testes de Path Traversal ==========

    @Test
    void testGetClientDocumentContent_WithPathTraversal_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com ../
        mockMvc.perform(get("/api/documents/client-content")
                        .param("personId", "1")
                        .param("templateName", "../../etc/passwd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Teste com ..\\
        mockMvc.perform(get("/api/documents/client-content")
                        .param("personId", "1")
                        .param("templateName", "..\\..\\windows\\system32")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Teste com caminho absoluto
        mockMvc.perform(get("/api/documents/client-content")
                        .param("personId", "1")
                        .param("templateName", "/etc/passwd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Teste com caminho absoluto Windows
        mockMvc.perform(get("/api/documents/client-content")
                        .param("personId", "1")
                        .param("templateName", "C:\\windows\\system32")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).getDocumentContentForClient(any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithPathTraversal_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "../../etc/passwd",
                    "content": {
                        "ops": [{"insert": "Test\\n"}]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGetClientDocumentContent_WithInvalidTemplateName_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com caracteres especiais
        mockMvc.perform(get("/api/documents/client-content")
                        .param("personId", "1")
                        .param("templateName", "template<script>.docx")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Teste sem extensão .docx
        mockMvc.perform(get("/api/documents/client-content")
                        .param("personId", "1")
                        .param("templateName", "template")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).getDocumentContentForClient(any(), any());
    }

    // ========== Testes de XSS e Injeção de Código ==========

    @Test
    void testGenerateCustomClientDocument_WithXSSInContent_RejectsInvalidDelta() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com atributo onclick (XSS)
        String requestBodyXSS = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {"onclick": "alert('xss')"}}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyXSS))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithScriptTag_SanitizesContent() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);
        when(wordDocumentService.generateCustomDocumentForClient(any(), any(), any())).thenReturn(new byte[0]);

        // Teste com script tag no texto - deve ser aceito e sanitizado pelo serviço
        // O texto será sanitizado durante a geração do documento Word
        String requestBodyScript = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "<script>alert('xss')</script>"}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyScript))
                .andExpect(status().isOk());

        // O serviço deve sanitizar o conteúdo durante a geração
        verify(wordDocumentService, times(1)).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithInvalidAttribute_RejectsInvalidDelta() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com atributo não permitido
        String requestBodyInvalid = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {"dangerous": "value"}}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyInvalid))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    // ========== Testes de DoS (Denial of Service) ==========

    @Test
    void testGenerateCustomClientDocument_WithTooManyOps_RejectsLargeDelta() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Criar Delta com mais de 10000 operações
        StringBuilder ops = new StringBuilder("[");
        for (int i = 0; i < 10001; i++) {
            if (i > 0) ops.append(",");
            ops.append("{\"insert\": \"text\"}");
        }
        ops.append("]");

        String requestBody = String.format("""
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": %s
                    }
                }
                """, ops.toString());

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithVeryLongInsert_RejectsLargeContent() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Criar texto muito longo (mais de 100000 caracteres)
        String longText = "a".repeat(100001);

        String requestBody = String.format("""
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "%s"}
                        ]
                    }
                }
                """, longText);

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    // ========== Testes de Estrutura Inválida ==========

    @Test
    void testGenerateCustomClientDocument_WithInvalidDeltaStructure_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste sem array ops
        String requestBodyNoOps = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "notOps": []
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyNoOps))
                .andExpect(status().isBadRequest());

        // Teste com ops não sendo array
        String requestBodyOpsNotArray = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": "not an array"
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyOpsNotArray))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithInvalidOperation_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com operação sem insert, retain ou delete
        String requestBodyInvalidOp = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"invalid": "operation"}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyInvalidOp))
                .andExpect(status().isBadRequest());

        // Teste com múltiplas operações na mesma op
        String requestBodyMultipleOps = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "test", "retain": 5}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyMultipleOps))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    // ========== Testes de Validação de Atributos ==========

    @Test
    void testGenerateCustomClientDocument_WithInvalidColorFormat_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com cor inválida
        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {"color": "invalid-color-format-12345"}}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithInvalidLink_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com link inválido (javascript:)
        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {"link": "javascript:alert('xss')"}}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithInvalidListType_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com tipo de lista inválido
        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {"list": "invalid"}}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithInvalidIndent_ReturnsBadRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);

        // Teste com indentação fora do range (1-10)
        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {"indent": 11}}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(wordDocumentService, never()).generateCustomDocumentForClient(any(), any(), any());
    }

    // ========== Testes de Validação Positiva (casos válidos) ==========

    @Test
    void testGenerateCustomClientDocument_WithValidDelta_AcceptsRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);
        when(wordDocumentService.generateCustomDocumentForClient(any(), any(), any())).thenReturn(new byte[0]);

        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Hello "},
                            {"insert": "World", "attributes": {"bold": true}},
                            {"insert": "\\n"}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(wordDocumentService, times(1)).generateCustomDocumentForClient(any(), any(), any());
    }

    @Test
    void testGenerateCustomClientDocument_WithValidAttributes_AcceptsRequest() throws Exception {
        when(personRepository.existsById(1L)).thenReturn(true);
        when(wordDocumentService.generateCustomDocumentForClient(any(), any(), any())).thenReturn(new byte[0]);

        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx",
                    "content": {
                        "ops": [
                            {"insert": "Test", "attributes": {
                                "bold": true,
                                "italic": true,
                                "underline": true,
                                "color": "#FF0000",
                                "align": "center",
                                "list": "ordered",
                                "indent": 1,
                                "header": 1
                            }}
                        ]
                    }
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client-custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(wordDocumentService, times(1)).generateCustomDocumentForClient(any(), any(), any());
    }
}

