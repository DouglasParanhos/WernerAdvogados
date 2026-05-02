package com.wa.controller;

import com.wa.dto.DatajudConsultaJobAcceptedDTO;
import com.wa.dto.DatajudConsultaJobStatusDTO;
import com.wa.service.DatajudConsultaJobService;
import com.wa.service.DatajudMovimentoConsultaService;
import com.wa.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DatajudController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-only-min-32-chars",
        "jwt.expiration=86400000",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class DatajudControllerAsyncTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatajudMovimentoConsultaService datajudMovimentoConsultaService;

    @MockBean
    private DatajudConsultaJobService datajudConsultaJobService;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    void postAsync_retornaAccepted() throws Exception {
        when(datajudConsultaJobService.startJob(any(), any(LocalDate.class)))
                .thenReturn(DatajudConsultaJobAcceptedDTO.builder()
                        .jobId("abc-uuid")
                        .status("PENDING")
                        .build());

        LocalDate d = LocalDate.now().minusDays(1);

        mockMvc.perform(post("/api/datajud/movimentos/async")
                        .param("dataInicio", d.toString()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").value("abc-uuid"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(datajudConsultaJobService).startJob(eq(""), eq(d));
    }

    @Test
    void getAsync_retornaNotFound_quandoServicoDevolveVazio() throws Exception {
        when(datajudConsultaJobService.getJob("id-x", "")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/datajud/movimentos/async/id-x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAsync_retornaPending() throws Exception {
        DatajudConsultaJobStatusDTO body = DatajudConsultaJobStatusDTO.builder()
                .jobId("j1")
                .status("PENDING")
                .build();
        when(datajudConsultaJobService.getJob("j1", "")).thenReturn(Optional.of(body));

        mockMvc.perform(get("/api/datajud/movimentos/async/j1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
