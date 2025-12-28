package com.wa.controller;

import com.wa.dto.MovimentDTO;
import com.wa.dto.MovimentRequestDTO;
import com.wa.service.MovimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moviments")
@RequiredArgsConstructor
public class MovimentController {
    
    private final MovimentService movimentService;
    
    @GetMapping
    public ResponseEntity<List<MovimentDTO>> findAll(@RequestParam(required = false) Long processId) {
        if (processId != null) {
            return ResponseEntity.ok(movimentService.findByProcessId(processId));
        }
        return ResponseEntity.ok(movimentService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MovimentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movimentService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<MovimentDTO> create(@Valid @RequestBody MovimentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MovimentDTO> update(@PathVariable Long id, @Valid @RequestBody MovimentRequestDTO request) {
        return ResponseEntity.ok(movimentService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movimentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

