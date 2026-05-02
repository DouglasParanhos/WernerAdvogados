package com.wa.service;

import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatajudConsultaJobAsyncRunnerTest {

    @Mock
    private DatajudMovimentoConsultaService datajudMovimentoConsultaService;

    @Mock
    private DatajudConsultaJobService datajudConsultaJobService;

    @InjectMocks
    private DatajudConsultaJobAsyncRunner runner;

    @Test
    void runConsulta_sucesso_marcaCompleted() {
        LocalDate ini = LocalDate.now().minusDays(2);
        var dto = DatajudMovimentoConsultaResponseDTO.builder().totalConsultados(3).build();
        when(datajudMovimentoConsultaService.consultarMovimentosDesde(ini)).thenReturn(dto);

        runner.runConsulta("job-1", ini);

        verify(datajudConsultaJobService).markCompleted("job-1", dto);
    }

    @Test
    void runConsulta_excecao_marcaFailed() {
        LocalDate ini = LocalDate.now().minusDays(2);
        when(datajudMovimentoConsultaService.consultarMovimentosDesde(ini))
                .thenThrow(new IllegalStateException("boom"));

        runner.runConsulta("job-2", ini);

        verify(datajudConsultaJobService).markFailed("job-2", "boom");
    }
}
