package com.wa.controller;

import com.wa.dto.MovimentDTO;
import com.wa.model.Person;
import com.wa.model.User;
import com.wa.repository.PersonRepository;
import com.wa.repository.UserRepository;
import com.wa.service.MovimentService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovimentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class MovimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimentService movimentService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PersonRepository personRepository;

    private MovimentDTO movimentDTO1;
    private MovimentDTO movimentDTO2;
    private User clientUser;
    private Person person;

    @BeforeEach
    void setUp() {
        movimentDTO1 = new MovimentDTO();
        movimentDTO1.setId(1L);
        movimentDTO1.setDescricao("Distribuição do processo");
        movimentDTO1.setDate(LocalDateTime.of(2023, 1, 15, 10, 0));
        movimentDTO1.setProcessId(1L);
        movimentDTO1.setProcessNumero("1234567-89.2023.8.19.0001");
        movimentDTO1.setProcessComarca("Capital");
        movimentDTO1.setProcessVara("1ª Vara");

        movimentDTO2 = new MovimentDTO();
        movimentDTO2.setId(2L);
        movimentDTO2.setDescricao("Juntada de documentos");
        movimentDTO2.setDate(LocalDateTime.of(2023, 2, 20, 14, 30));
        movimentDTO2.setProcessId(1L);
        movimentDTO2.setProcessNumero("1234567-89.2023.8.19.0001");
        movimentDTO2.setProcessComarca("Capital");
        movimentDTO2.setProcessVara("1ª Vara");

        clientUser = new User();
        clientUser.setId(1L);
        clientUser.setUsername("joao.silva");
        clientUser.setRole("CLIENT");

        person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setUser(clientUser);
    }

    @Test
    void testGetMyMoviments_WithClientRole_ReturnsMoviments() throws Exception {
        // Arrange
        List<MovimentDTO> moviments = List.of(movimentDTO1, movimentDTO2);

        // Mock SecurityContext with CLIENT role
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("joao.silva");
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        when(userRepository.findByUsername("joao.silva")).thenReturn(Optional.of(clientUser));
        when(personRepository.findByUserId(1L)).thenReturn(Optional.of(person));
        when(movimentService.findByPersonId(1L)).thenReturn(moviments);

        // Act & Assert
        mockMvc.perform(get("/api/moviments/my-moviments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descricao").value("Distribuição do processo"))
                .andExpect(jsonPath("$[0].processNumero").value("1234567-89.2023.8.19.0001"))
                .andExpect(jsonPath("$[0].processComarca").value("Capital"))
                .andExpect(jsonPath("$[0].processVara").value("1ª Vara"))
                .andExpect(jsonPath("$[1].descricao").value("Juntada de documentos"));

        verify(movimentService, times(1)).findByPersonId(1L);
    }

    @Test
    void testGetMyMoviments_WithNoMoviments_ReturnsEmptyList() throws Exception {
        // Arrange
        // Mock SecurityContext with CLIENT role
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("joao.silva");
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        when(userRepository.findByUsername("joao.silva")).thenReturn(Optional.of(clientUser));
        when(personRepository.findByUserId(1L)).thenReturn(Optional.of(person));
        when(movimentService.findByPersonId(1L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/moviments/my-moviments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(movimentService, times(1)).findByPersonId(1L);
    }

    @Test
    void testGetMyMoviments_WithNonClientRole_ReturnsForbidden() throws Exception {
        // Arrange
        // Mock SecurityContext with ADMIN role (not CLIENT)
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Act & Assert
        mockMvc.perform(get("/api/moviments/my-moviments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(movimentService, never()).findByPersonId(any());
    }

    @Test
    void testGetMyMoviments_UserNotFound_ThrowsException() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("unknown.user");
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        when(userRepository.findByUsername("unknown.user")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/moviments/my-moviments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(movimentService, never()).findByPersonId(any());
    }

    @Test
    void testGetMyMoviments_PersonNotFound_ThrowsException() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("joao.silva");
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        when(userRepository.findByUsername("joao.silva")).thenReturn(Optional.of(clientUser));
        when(personRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/moviments/my-moviments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(movimentService, never()).findByPersonId(any());
    }
}

