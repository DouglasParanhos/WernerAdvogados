package com.wa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatajudConsultaJobAsyncRunner {

    private final DatajudMovimentoConsultaService datajudMovimentoConsultaService;
    private final DatajudConsultaJobService datajudConsultaJobService;

    @Async("datajudConsultaExecutor")
    public void runConsulta(String jobId, LocalDate dataInicio) {
        try {
            var result = datajudMovimentoConsultaService.consultarMovimentosDesde(dataInicio);
            datajudConsultaJobService.markCompleted(jobId, result);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            datajudConsultaJobService.markFailed(jobId, msg);
            log.debug("DataJud job {} exceção", jobId, e);
        }
    }
}
