package com.wa.controller;

import com.wa.dto.MatriculationDTO;
import com.wa.dto.MatriculationRequestDTO;
import com.wa.service.MatriculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculations")
@RequiredArgsConstructor
public class MatriculationController {
    
    private final MatriculationService matriculationService;
    
    @GetMapping
    public ResponseEntity<List<MatriculationDTO>> findAll(@RequestParam(required = false) Long personId) {
        if (personId != null) {
            return ResponseEntity.ok(matriculationService.findByPersonId(personId));
        }
        return ResponseEntity.ok(matriculationService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MatriculationDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matriculationService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<MatriculationDTO> create(@Valid @RequestBody MatriculationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculationService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MatriculationDTO> update(@PathVariable Long id, @Valid @RequestBody MatriculationRequestDTO request) {
        return ResponseEntity.ok(matriculationService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matriculationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

