package com.wa.controller;

import com.wa.dto.PersonDTO;
import com.wa.dto.PersonRequestDTO;
import com.wa.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {
    
    private final PersonService personService;
    
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search) {
        // Se page ou size forem fornecidos, usar paginação
        if (page != null || size != null) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? size : 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PersonDTO> result = personService.findAllPaginated(search, pageable);
            return ResponseEntity.ok(result);
        }
        // Caso contrário, retornar lista completa (compatibilidade)
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

