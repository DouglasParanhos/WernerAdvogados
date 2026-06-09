package com.wa.controller;

import com.wa.annotation.RequiresNonClient;
import com.wa.dto.RecursoDTO;
import com.wa.dto.RecursoRequestDTO;
import com.wa.service.RecursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final RecursoService recursoService;

    @GetMapping
    @RequiresNonClient
    public ResponseEntity<List<RecursoDTO>> findByProcessId(@RequestParam Long processId) {
        return ResponseEntity.ok(recursoService.findByProcessId(processId));
    }

    @GetMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<RecursoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(recursoService.findById(id));
    }

    @PostMapping
    @RequiresNonClient
    public ResponseEntity<RecursoDTO> create(@Valid @RequestBody RecursoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recursoService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<RecursoDTO> update(@PathVariable Long id, @Valid @RequestBody RecursoRequestDTO request) {
        return ResponseEntity.ok(recursoService.update(id, request));
    }

    @PatchMapping("/{id}/baixar")
    @RequiresNonClient
    public ResponseEntity<RecursoDTO> baixar(@PathVariable Long id) {
        return ResponseEntity.ok(recursoService.baixar(id));
    }

    @DeleteMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
