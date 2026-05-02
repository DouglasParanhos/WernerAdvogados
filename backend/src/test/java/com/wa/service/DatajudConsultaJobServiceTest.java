package com.wa.service;

import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DatajudConsultaJobServiceTest {

    @Mock
    private DatajudMovimentoConsultaService datajudMovimentoConsultaService;

    @Mock
    private DatajudConsultaJobAsyncRunner asyncRunner;

    private DatajudConsultaJobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new DatajudConsultaJobService(datajudMovimentoConsultaService, asyncRunner);
        lenient().doNothing().when(asyncRunner).runConsulta(anyString(), any());
    }

    @Test
    void startJob_chamaValidacaoEDisparaRunner() {
        LocalDate ini = LocalDate.now().minusDays(1);

        var accepted = jobService.startJob("user1", ini);

        assertThat(accepted.getJobId()).isNotBlank();
        assertThat(accepted.getStatus()).isEqualTo(DatajudConsultaJobService.STATUS_PENDING);
        verify(datajudMovimentoConsultaService).validarConsultaMovimentosDesde(ini);
        verify(asyncRunner).runConsulta(eq(accepted.getJobId()), eq(ini));
        assertThat(jobService.getJob(accepted.getJobId(), "user1"))
                .isPresent()
                .get()
                .satisfies(s -> assertThat(s.getStatus()).isEqualTo(DatajudConsultaJobService.STATUS_PENDING));
    }

    @Test
    void startJob_validacaoFalha_naoAgendaRunner() {
        LocalDate ini = LocalDate.now().minusDays(1);
        org.mockito.Mockito.doThrow(new IllegalArgumentException("bad"))
                .when(datajudMovimentoConsultaService)
                .validarConsultaMovimentosDesde(ini);

        assertThatThrownBy(() -> jobService.startJob("u", ini)).isInstanceOf(IllegalArgumentException.class);
        verify(asyncRunner, never()).runConsulta(anyString(), any());
    }

    @Test
    void getJob_ownerDiferente_vazio() {
        LocalDate ini = LocalDate.now().minusDays(1);

        String jobId = jobService.startJob("alice", ini).getJobId();

        assertThat(jobService.getJob(jobId, "bob")).isEmpty();
    }

    @Test
    void markCompleted_permiteConsulta() {
        LocalDate ini = LocalDate.now().minusDays(1);
        String jobId = jobService.startJob("u", ini).getJobId();
        var result = DatajudMovimentoConsultaResponseDTO.builder()
                .totalConsultados(0)
                .build();

        jobService.markCompleted(jobId, result);

        assertThat(jobService.getJob(jobId, "u"))
                .isPresent()
                .get()
                .satisfies(s -> {
                    assertThat(s.getStatus()).isEqualTo(DatajudConsultaJobService.STATUS_COMPLETED);
                    assertThat(s.getResult()).isSameAs(result);
                });
    }
}
