package com.wa.controller;

import com.wa.dto.PersonDTO;
import com.wa.service.PersonService;
import com.wa.util.JWTUtil;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private JWTUtil jwtUtil;

    private PersonDTO personDTO1;
    private PersonDTO personDTO2;
    private PersonDTO personDTO3;

    @BeforeEach
    void setUp() {
        personDTO1 = new PersonDTO();
        personDTO1.setId(1L);
        personDTO1.setFullname("João Silva");
        personDTO1.setEmail("joao@example.com");
        personDTO1.setCpf("12345678900");

        personDTO2 = new PersonDTO();
        personDTO2.setId(2L);
        personDTO2.setFullname("Maria Santos");
        personDTO2.setEmail("maria@example.com");
        personDTO2.setCpf("98765432100");

        personDTO3 = new PersonDTO();
        personDTO3.setId(3L);
        personDTO3.setFullname("Pedro Oliveira");
        personDTO3.setEmail("pedro@example.com");
        personDTO3.setCpf("11122233344");
    }

    @Test
    void testFindAll_WithPagination_ReturnsPaginatedResponse() throws Exception {
        // Arrange
        List<PersonDTO> persons = List.of(personDTO1, personDTO2, personDTO3);
        Page<PersonDTO> page = new PageImpl<>(persons, PageRequest.of(0, 10), 3);

        when(personService.findAllPaginated(any(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.content[0].fullname").value("João Silva"))
                .andExpect(jsonPath("$.content[1].fullname").value("Maria Santos"))
                .andExpect(jsonPath("$.content[2].fullname").value("Pedro Oliveira"));

        verify(personService, times(1)).findAllPaginated(null, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithSearch_ReturnsFilteredResults() throws Exception {
        // Arrange
        String search = "João";
        List<PersonDTO> filteredPersons = List.of(personDTO1);
        Page<PersonDTO> page = new PageImpl<>(filteredPersons, PageRequest.of(0, 10), 1);

        when(personService.findAllPaginated(eq(search), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", search)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].fullname").value("João Silva"));

        verify(personService, times(1)).findAllPaginated(search, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithPageOnly_UsesDefaultSize() throws Exception {
        // Arrange
        List<PersonDTO> persons = List.of(personDTO1, personDTO2);
        Page<PersonDTO> page = new PageImpl<>(persons, PageRequest.of(0, 10), 2);

        when(personService.findAllPaginated(any(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10));

        verify(personService, times(1)).findAllPaginated(null, PageRequest.of(0, 10));
    }

    @Test
    void testFindAll_WithSizeOnly_UsesDefaultPage() throws Exception {
        // Arrange
        List<PersonDTO> persons = List.of(personDTO1, personDTO2, personDTO3);
        Page<PersonDTO> page = new PageImpl<>(persons, PageRequest.of(0, 50), 3);

        when(personService.findAllPaginated(any(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.number").value(0));

        verify(personService, times(1)).findAllPaginated(null, PageRequest.of(0, 50));
    }

    @Test
    void testFindAll_WithDifferentPageSizes() throws Exception {
        // Arrange
        List<PersonDTO> persons = List.of(personDTO1, personDTO2, personDTO3);
        Page<PersonDTO> page10 = new PageImpl<>(persons, PageRequest.of(0, 10), 3);
        Page<PersonDTO> page50 = new PageImpl<>(persons, PageRequest.of(0, 50), 3);
        Page<PersonDTO> page100 = new PageImpl<>(persons, PageRequest.of(0, 100), 3);

        when(personService.findAllPaginated(null, PageRequest.of(0, 10))).thenReturn(page10);
        when(personService.findAllPaginated(null, PageRequest.of(0, 50))).thenReturn(page50);
        when(personService.findAllPaginated(null, PageRequest.of(0, 100))).thenReturn(page100);

        // Act & Assert - Size 10
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10));

        // Act & Assert - Size 50
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));

        // Act & Assert - Size 100
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100));
    }

    @Test
    void testFindAll_WithoutPaginationParams_ReturnsList() throws Exception {
        // Arrange
        List<PersonDTO> persons = List.of(personDTO1, personDTO2, personDTO3);
        when(personService.findAll()).thenReturn(persons);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(personService, times(1)).findAll();
        verify(personService, never()).findAllPaginated(any(), any());
    }

    @Test
    void testFindAll_WithSecondPage_ReturnsCorrectPage() throws Exception {
        // Arrange
        List<PersonDTO> page2Persons = List.of(personDTO3);
        Page<PersonDTO> page = new PageImpl<>(page2Persons, PageRequest.of(1, 2), 3);

        when(personService.findAllPaginated(any(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalElements").value(3));

        verify(personService, times(1)).findAllPaginated(null, PageRequest.of(1, 2));
    }

    @Test
    void testGetUsernameSuggestion_ReturnsSuggestion() throws Exception {
        // Arrange
        when(personService.generateUsernameSuggestion(1L)).thenReturn("joao.silva");

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons/1/username-suggestion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao.silva"));

        verify(personService, times(1)).generateUsernameSuggestion(1L);
    }

    @Test
    void testConfigureCredentials_WithAdminRole_ReturnsOk() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "username": "joao.silva",
                    "password": "Test123!"
                }
                """;

        // Mock SecurityContext with ADMIN role
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        doNothing().when(personService).createOrUpdateCredentials(eq(1L), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/persons/1/credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(personService, times(1)).createOrUpdateCredentials(eq(1L), any());
    }

    @Test
    void testConfigureCredentials_WithSuperAdminRole_ReturnsOk() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "username": "joao.silva",
                    "password": "Test123!"
                }
                """;

        // Mock SecurityContext with SUPERADMIN role
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMIN")));

        doNothing().when(personService).createOrUpdateCredentials(eq(1L), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/persons/1/credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(personService, times(1)).createOrUpdateCredentials(eq(1L), any());
    }

    @Test
    void testFindById_WithUsername_ReturnsUsername() throws Exception {
        // Arrange
        PersonDTO personWithUser = new PersonDTO();
        personWithUser.setId(1L);
        personWithUser.setFullname("João Silva");
        personWithUser.setEmail("joao@example.com");
        personWithUser.setCpf("12345678900");
        personWithUser.setUsername("joao.silva");
        personWithUser.setUserId(1L);

        when(personService.findById(1L)).thenReturn(personWithUser);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullname").value("João Silva"))
                .andExpect(jsonPath("$.username").value("joao.silva"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(personService, times(1)).findById(1L);
    }

    @Test
    void testFindById_WithoutUsername_UsernameIsNull() throws Exception {
        // Arrange
        PersonDTO personWithoutUser = new PersonDTO();
        personWithoutUser.setId(1L);
        personWithoutUser.setFullname("João Silva");
        personWithoutUser.setEmail("joao@example.com");
        personWithoutUser.setCpf("12345678900");
        personWithoutUser.setUsername(null);
        personWithoutUser.setUserId(null);

        when(personService.findById(1L)).thenReturn(personWithoutUser);

        // Act & Assert
        mockMvc.perform(get("/api/v1/persons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullname").value("João Silva"))
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.userId").doesNotExist());

        verify(personService, times(1)).findById(1L);
    }
}

