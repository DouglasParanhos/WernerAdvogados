package com.wa.controller;

import com.wa.model.Person;
import com.wa.model.User;
import com.wa.repository.PersonRepository;
import com.wa.repository.UserRepository;
import com.wa.service.DocumentTemplateService;
import com.wa.service.WordDocumentService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes específicos para verificar acesso de CLIENT aos endpoints de documentos
 */
@WebMvcTest(controllers = DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class DocumentControllerClientAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentTemplateService templateService;

    @MockBean
    private WordDocumentService wordDocumentService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private UserRepository userRepository;

    private User clientUser;
    private Person clientPerson;
    private Person otherPerson;
    private Authentication clientAuthentication;
    private Authentication adminAuthentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        clientUser = new User();
        clientUser.setId(1L);
        clientUser.setUsername("client.user");
        clientUser.setRole("CLIENT");

        clientPerson = new Person();
        clientPerson.setId(1L);
        clientPerson.setFullname("Client Person");
        clientPerson.setUser(clientUser);

        otherPerson = new Person();
        otherPerson.setId(2L);
        otherPerson.setFullname("Other Person");

        // Setup CLIENT authentication
        clientAuthentication = mock(Authentication.class);
        when(clientAuthentication.isAuthenticated()).thenReturn(true);
        when(clientAuthentication.getName()).thenReturn("client.user");
        when(clientAuthentication.getAuthorities()).thenAnswer(invocation ->
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        // Setup ADMIN authentication
        adminAuthentication = mock(Authentication.class);
        when(adminAuthentication.isAuthenticated()).thenReturn(true);
        when(adminAuthentication.getName()).thenReturn("admin.user");
        when(adminAuthentication.getAuthorities()).thenAnswer(invocation ->
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetClientTemplates_WithClientRole_ReturnsOk() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(clientAuthentication);
        when(personRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/api/documents/client-templates")
                        .param("personId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGenerateClientDocument_WithClientRole_OwnPersonId_ReturnsOk() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(clientAuthentication);
        when(userRepository.findByUsername("client.user")).thenReturn(Optional.of(clientUser));
        when(personRepository.findByUserId(1L)).thenReturn(Optional.of(clientPerson));
        when(wordDocumentService.generateDocumentForClient(eq(1L), any())).thenReturn(new byte[0]);

        String requestBody = """
                {
                    "personId": 1,
                    "templateName": "template.docx"
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(wordDocumentService, times(1)).generateDocumentForClient(eq(1L), any());
    }

    @Test
    void testGenerateClientDocument_WithClientRole_OtherPersonId_ReturnsForbidden() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(clientAuthentication);
        when(userRepository.findByUsername("client.user")).thenReturn(Optional.of(clientUser));
        when(personRepository.findByUserId(1L)).thenReturn(Optional.of(clientPerson));

        String requestBody = """
                {
                    "personId": 2,
                    "templateName": "template.docx"
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(wordDocumentService, never()).generateDocumentForClient(any(), any());
    }

    @Test
    void testGenerateClientDocument_WithAdminRole_AnyPersonId_ReturnsOk() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(adminAuthentication);
        when(wordDocumentService.generateDocumentForClient(eq(2L), any())).thenReturn(new byte[0]);

        String requestBody = """
                {
                    "personId": 2,
                    "templateName": "template.docx"
                }
                """;

        mockMvc.perform(post("/api/documents/generate-client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(wordDocumentService, times(1)).generateDocumentForClient(eq(2L), any());
    }

    @Test
    void testGetTemplates_WithClientRole_ReturnsForbidden() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(clientAuthentication);

        mockMvc.perform(get("/api/documents/templates")
                        .param("processId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGenerateDocument_WithClientRole_ReturnsForbidden() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(clientAuthentication);

        String requestBody = """
                {
                    "processId": 1,
                    "templateName": "template.docx"
                }
                """;

        mockMvc.perform(post("/api/documents/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}

