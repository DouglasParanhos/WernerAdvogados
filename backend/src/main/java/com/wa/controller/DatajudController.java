package com.wa.controller;

import com.wa.annotation.RequiresNonClient;
import com.wa.dto.DatajudConsultaJobAcceptedDTO;
import com.wa.dto.DatajudConsultaJobStatusDTO;
import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import com.wa.dto.DatajudProcessoMovimentoDTO;
import com.wa.service.DatajudConsultaJobService;
import com.wa.service.DatajudMovimentoConsultaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/datajud")
@RequiredArgsConstructor
public class DatajudController {

    private final DatajudMovimentoConsultaService datajudMovimentoConsultaService;
    private final DatajudConsultaJobService datajudConsultaJobService;

    /**
     * Lista movimentos DataJud TJRJ desde {@code dataInicio} (00:00 America/Sao_Paulo) até o momento atual,
     * para cada processo cadastrado com NPU TJRJ (.8.19.).
     */
    @GetMapping("/movimentos")
    @RequiresNonClient
    public ResponseEntity<DatajudMovimentoConsultaResponseDTO> consultarMovimentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio
    ) {
        try {
            DatajudMovimentoConsultaResponseDTO body =
                    datajudMovimentoConsultaService.consultarMovimentosDesde(dataInicio);
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }
    }

    /**
     * Inicia consulta DataJud em lote de forma assíncrona. Use GET {@code /movimentos/async/{jobId}} para acompanhar.
     */
    @PostMapping("/movimentos/async")
    @RequiresNonClient
    public ResponseEntity<DatajudConsultaJobAcceptedDTO> iniciarConsultaMovimentosAsync(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String owner = auth != null ? auth.getName() : "";
            DatajudConsultaJobAcceptedDTO body =
                    datajudConsultaJobService.startJob(owner, dataInicio);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(body);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }
    }

    @GetMapping("/movimentos/async/{jobId}")
    @RequiresNonClient
    public ResponseEntity<DatajudConsultaJobStatusDTO> consultarStatusJob(
            @PathVariable String jobId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String owner = auth != null ? auth.getName() : "";
        return datajudConsultaJobService.getJob(jobId, owner)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job não encontrado."));
    }

    /**
     * Movimentos DataJud TJRJ para um processo (NPU com segmento .8.19.), no intervalo da data inicial (Brasília) até agora.
     */
    @GetMapping("/movimentos/processo/{processId}")
    @RequiresNonClient
    public ResponseEntity<DatajudProcessoMovimentoDTO> consultarMovimentosProcesso(
            @PathVariable Long processId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio
    ) {
        try {
            return ResponseEntity.ok(
                    datajudMovimentoConsultaService.consultarMovimentosProcesso(processId, dataInicio));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }
    }
}
