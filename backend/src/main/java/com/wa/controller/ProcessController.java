package com.wa.controller;

import com.wa.dto.ProcessDTO;
import com.wa.dto.ProcessRequestDTO;
import com.wa.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<ProcessDTO>> findAll(@RequestParam(required = false) Long personId) {
        if (personId != null) {
            return ResponseEntity.ok(processService.findByPersonId(personId));
        }
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

