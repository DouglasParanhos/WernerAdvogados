package com.wa.controller;

import com.wa.annotation.RequiresNonClient;
import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import com.wa.service.DatajudMovimentoConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
