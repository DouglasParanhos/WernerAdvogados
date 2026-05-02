package com.wa.service;

import com.wa.dto.DatajudConsultaJobAcceptedDTO;
import com.wa.dto.DatajudConsultaJobStatusDTO;
import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DatajudConsultaJobService {

    static final String STATUS_PENDING = "PENDING";
    static final String STATUS_COMPLETED = "COMPLETED";
    static final String STATUS_FAILED = "FAILED";

    private static final Duration JOB_TTL = Duration.ofHours(1);

    private final DatajudMovimentoConsultaService datajudMovimentoConsultaService;
    private final DatajudConsultaJobAsyncRunner asyncRunner;

    public DatajudConsultaJobService(
            DatajudMovimentoConsultaService datajudMovimentoConsultaService,
            @Lazy DatajudConsultaJobAsyncRunner asyncRunner) {
        this.datajudMovimentoConsultaService = datajudMovimentoConsultaService;
        this.asyncRunner = asyncRunner;
    }

    private final Map<String, JobEntry> jobs = new ConcurrentHashMap<>();

    public DatajudConsultaJobAcceptedDTO startJob(String owner, LocalDate dataInicio) {
        datajudMovimentoConsultaService.validarConsultaMovimentosDesde(dataInicio);
        String jobId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        jobs.put(jobId, new JobEntry(jobId, owner, dataInicio, STATUS_PENDING, now, null, null, null));
        log.info("DataJud job {} criado para owner={}, dataInicio={}", jobId, owner, dataInicio);
        asyncRunner.runConsulta(jobId, dataInicio);
        return DatajudConsultaJobAcceptedDTO.builder()
                .jobId(jobId)
                .status(STATUS_PENDING)
                .build();
    }

    /**
     * @return empty se o job não existir, expirou ou o utilizador não é o dono
     */
    public Optional<DatajudConsultaJobStatusDTO> getJob(String jobId, String requester) {
        removeIfExpired(jobId);
        JobEntry e = jobs.get(jobId);
        if (e == null) {
            return Optional.empty();
        }
        if (!e.owner.equals(requester)) {
            return Optional.empty();
        }
        return Optional.of(toDto(e));
    }

    public void markCompleted(String jobId, DatajudMovimentoConsultaResponseDTO result) {
        JobEntry e = jobs.get(jobId);
        if (e == null) {
            log.warn("DataJud job {} concluído mas registo já removido", jobId);
            return;
        }
        synchronized (e) {
            e.status = STATUS_COMPLETED;
            e.result = result;
            e.errorMessage = null;
            e.finishedAt = Instant.now();
        }
        log.info("DataJud job {} COMPLETED", jobId);
    }

    public void markFailed(String jobId, String errorMessage) {
        JobEntry e = jobs.get(jobId);
        if (e == null) {
            log.warn("DataJud job {} falhou mas registo já removido", jobId);
            return;
        }
        synchronized (e) {
            e.status = STATUS_FAILED;
            e.result = null;
            e.errorMessage = errorMessage;
            e.finishedAt = Instant.now();
        }
        log.warn("DataJud job {} FAILED: {}", jobId, errorMessage);
    }

    @Scheduled(fixedRate = 600_000)
    public void purgeExpiredJobs() {
        Instant cutoff = Instant.now().minus(JOB_TTL);
        jobs.entrySet().removeIf(entry -> entry.getValue().createdAt.isBefore(cutoff));
    }

    private void removeIfExpired(String jobId) {
        JobEntry e = jobs.get(jobId);
        if (e != null && e.createdAt.isBefore(Instant.now().minus(JOB_TTL))) {
            jobs.remove(jobId, e);
        }
    }

    private static DatajudConsultaJobStatusDTO toDto(JobEntry e) {
        return DatajudConsultaJobStatusDTO.builder()
                .jobId(e.jobId)
                .status(e.status)
                .result(e.result)
                .errorMessage(e.errorMessage)
                .build();
    }

    static final class JobEntry {
        final String jobId;
        final String owner;
        final LocalDate dataInicio;
        volatile String status;
        final Instant createdAt;
        volatile Instant finishedAt;
        volatile DatajudMovimentoConsultaResponseDTO result;
        volatile String errorMessage;

        JobEntry(
                String jobId,
                String owner,
                LocalDate dataInicio,
                String status,
                Instant createdAt,
                Instant finishedAt,
                DatajudMovimentoConsultaResponseDTO result,
                String errorMessage) {
            this.jobId = jobId;
            this.owner = owner;
            this.dataInicio = dataInicio;
            this.status = status;
            this.createdAt = createdAt;
            this.finishedAt = finishedAt;
            this.result = result;
            this.errorMessage = errorMessage;
        }
    }
}
