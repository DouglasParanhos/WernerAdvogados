package com.wa.controller;

import com.wa.annotation.RequiresNonClient;
import com.wa.dto.ClientCredentialsDTO;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {
    
    private final PersonService personService;
    
    @GetMapping
    @RequiresNonClient
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
    @RequiresNonClient
    public ResponseEntity<PersonDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.findById(id));
    }
    
    @PostMapping
    @RequiresNonClient
    public ResponseEntity<PersonDTO> create(@Valid @RequestBody PersonRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(request));
    }
    
    @PutMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<PersonDTO> update(@PathVariable Long id, @Valid @RequestBody PersonRequestDTO request) {
        return ResponseEntity.ok(personService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @RequiresNonClient
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/username-suggestion")
    @RequiresNonClient
    public ResponseEntity<Map<String, String>> getUsernameSuggestion(@PathVariable Long id) {
        String suggestion = personService.generateUsernameSuggestion(id);
        return ResponseEntity.ok(Map.of("username", suggestion));
    }

    @PostMapping("/{id}/credentials")
    public ResponseEntity<Void> configureCredentials(
            @PathVariable Long id,
            @Valid @RequestBody ClientCredentialsDTO credentials) {
        // Verificar se o usuário autenticado tem role ADMIN ou SUPERADMIN
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Acesso negado");
        }

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN") || authority.equals("ROLE_SUPERADMIN"));

        if (!hasAdminRole) {
            throw new AccessDeniedException("Apenas ADMIN ou SUPERADMIN podem configurar credenciais");
        }

        personService.createOrUpdateCredentials(id, credentials);
        return ResponseEntity.ok().build();
    }
}

