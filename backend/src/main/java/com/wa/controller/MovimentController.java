package com.wa.controller;

import com.wa.annotation.RequiresNonClient;
import com.wa.dto.MovimentDTO;
import com.wa.dto.MovimentRequestDTO;
import com.wa.model.Person;
import com.wa.model.User;
import com.wa.repository.PersonRepository;
import com.wa.repository.UserRepository;
import com.wa.service.MovimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moviments")
@RequiredArgsConstructor
public class MovimentController {
    
    private final MovimentService movimentService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    
    @GetMapping
    @RequiresNonClient
    public ResponseEntity<List<MovimentDTO>> findAll(@RequestParam(required = false) Long processId) {
        if (processId != null) {
            return ResponseEntity.ok(movimentService.findByProcessId(processId));
        }
        return ResponseEntity.ok(movimentService.findAll());
    }
    
    @GetMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<MovimentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movimentService.findById(id));
    }
    
    @PostMapping
    @RequiresNonClient
    public ResponseEntity<MovimentDTO> create(@Valid @RequestBody MovimentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentService.create(request));
    }
    
    @PutMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<MovimentDTO> update(@PathVariable Long id, @Valid @RequestBody MovimentRequestDTO request) {
        return ResponseEntity.ok(movimentService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movimentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-moviments")
    public ResponseEntity<List<MovimentDTO>> getMyMoviments() {
        // Verificar se o usuário autenticado tem role CLIENT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Acesso negado");
        }

        boolean hasClientRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_CLIENT"));

        if (!hasClientRole) {
            throw new AccessDeniedException("Apenas clientes podem acessar suas movimentações");
        }

        // Buscar Person associado ao User logado
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Person person = personRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para este usuário"));

        List<MovimentDTO> moviments = movimentService.findByPersonId(person.getId());
        return ResponseEntity.ok(moviments);
    }
}

