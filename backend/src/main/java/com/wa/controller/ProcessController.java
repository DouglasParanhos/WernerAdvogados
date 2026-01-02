package com.wa.controller;

import com.wa.dto.ProcessDTO;
import com.wa.dto.ProcessRequestDTO;
import com.wa.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
public class ProcessController {
    
    private final ProcessService processService;
    
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Long personId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) String vara,
            @RequestParam(required = false) String tipoProcesso,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean showArchived) {
        // Se personId for fornecido, retornar processos da pessoa (compatibilidade)
        if (personId != null && page == null && size == null) {
            return ResponseEntity.ok(processService.findByPersonId(personId));
        }
        // Se page ou size forem fornecidos, usar paginação
        if (page != null || size != null) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? size : 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Boolean showArchivedValue = showArchived != null ? showArchived : false;
            Page<ProcessDTO> result = processService.findAllPaginated(
                    numero, comarca, vara, tipoProcesso, status, showArchivedValue, pageable);
            return ResponseEntity.ok(result);
        }
        // Caso contrário, retornar lista completa (compatibilidade)
        return ResponseEntity.ok(processService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProcessDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(processService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<ProcessDTO> create(@Valid @RequestBody ProcessRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProcessDTO> update(@PathVariable Long id, @Valid @RequestBody ProcessRequestDTO request) {
        return ResponseEntity.ok(processService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        processService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/distinct")
    public ResponseEntity<List<String>> getDistinctStatuses() {
        return ResponseEntity.ok(processService.getDistinctStatuses());
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProcessDTO> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        String status = request.get("status");
        return ResponseEntity.ok(processService.updateStatus(id, status));
    }
}

