package com.wa.controller;

import com.wa.dto.PersonDTO;
import com.wa.dto.PersonRequestDTO;
import com.wa.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {
    
    private final PersonService personService;
    
    @GetMapping
    public ResponseEntity<List<PersonDTO>> findAll() {
        return ResponseEntity.ok(personService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<PersonDTO> create(@Valid @RequestBody PersonRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> update(@PathVariable Long id, @Valid @RequestBody PersonRequestDTO request) {
        return ResponseEntity.ok(personService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

