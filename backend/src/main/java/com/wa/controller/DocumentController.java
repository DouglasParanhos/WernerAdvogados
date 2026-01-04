package com.wa.controller;

import com.wa.annotation.RequiresNonClient;
import com.wa.dto.ClientDocumentGenerationRequestDTO;
import com.wa.dto.DocumentGenerationRequestDTO;
import com.wa.dto.DocumentTemplateDTO;
import com.wa.model.Person;
import com.wa.model.User;
import com.wa.repository.PersonRepository;
import com.wa.repository.ProcessRepository;
import com.wa.repository.UserRepository;
import com.wa.service.DocumentTemplateService;
import com.wa.service.WordDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {
    
    private final DocumentTemplateService templateService;
    private final WordDocumentService wordDocumentService;
    private final ProcessRepository processRepository;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    
    /**
     * Lista templates disponíveis para um processo específico
     */
    @GetMapping("/templates")
    @RequiresNonClient
    public ResponseEntity<List<DocumentTemplateDTO>> getTemplates(@RequestParam Long processId) {
        try {
            log.debug("Buscando templates para processo ID: {}", processId);
            
            // Buscar tipo do processo
            String tipoProcesso = processRepository.findById(processId)
                    .map(process -> {
                        log.debug("Processo encontrado: tipoProcesso = {}", process.getTipoProcesso());
                        return process.getTipoProcesso();
                    })
                    .orElse(null);
            
            if (tipoProcesso == null) {
                log.warn("Processo {} não encontrado ou sem tipoProcesso definido", processId);
                return ResponseEntity.ok(List.of());
            }
            
            // Filtrar templates por tipo
            List<DocumentTemplateDTO> templates = templateService.getTemplatesByProcessType(tipoProcesso);
            
            log.debug("Retornando {} templates para processo tipo '{}'", templates.size(), tipoProcesso);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Erro ao buscar templates: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }
    
    /**
     * Gera um documento Word e retorna para download
     */
    @PostMapping("/generate")
    @RequiresNonClient
    public ResponseEntity<ByteArrayResource> generateDocument(
            @Valid @RequestBody DocumentGenerationRequestDTO request) {
        try {
            // Gerar documento
            byte[] documentBytes = wordDocumentService.generateDocument(
                    request.getProcessId(), 
                    request.getTemplateName()
            );
            
            // Criar recurso para download
            ByteArrayResource resource = new ByteArrayResource(documentBytes);
            
            // Nome do arquivo gerado
            String fileName = request.getTemplateName().replace(".docx", "") + "_gerado.docx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(documentBytes.length)
                    .body(resource);
                    
        } catch (RuntimeException e) {
            log.error("Erro ao gerar documento: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Erro de IO ao gerar documento: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lista templates disponíveis para um cliente específico
     */
    @GetMapping("/client-templates")
    public ResponseEntity<List<DocumentTemplateDTO>> getClientTemplates(@RequestParam Long personId) {
        try {
            log.debug("Buscando templates para cliente ID: {}", personId);
            
            // Verificar se cliente existe
            if (!personRepository.existsById(personId)) {
                log.warn("Cliente {} não encontrado", personId);
                return ResponseEntity.ok(List.of());
            }
            
            // Filtrar templates por categoria CLIENT
            List<DocumentTemplateDTO> templates = templateService.getTemplatesByCategory("CLIENT");
            
            log.debug("Retornando {} templates para cliente", templates.size());
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Erro ao buscar templates de cliente: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }
    
    /**
     * Gera um documento Word usando dados do cliente e retorna para download
     */
    @PostMapping("/generate-client")
    public ResponseEntity<ByteArrayResource> generateClientDocument(
            @Valid @RequestBody ClientDocumentGenerationRequestDTO request) {
        try {
            // Verificar se o usuário autenticado tem role CLIENT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                boolean isClient = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(authority -> authority.equals("ROLE_CLIENT"));
                
                if (isClient) {
                    // CLIENT só pode gerar documentos para si mesmo
                    String username = authentication.getName();
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    Person person = personRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new RuntimeException("Cliente não encontrado para este usuário"));
                    
                    if (!person.getId().equals(request.getPersonId())) {
                        throw new AccessDeniedException("Clientes só podem gerar documentos para si mesmos");
                    }
                }
            }
            
            // Gerar documento
            byte[] documentBytes = wordDocumentService.generateDocumentForClient(
                    request.getPersonId(), 
                    request.getTemplateName()
            );
            
            // Criar recurso para download
            ByteArrayResource resource = new ByteArrayResource(documentBytes);
            
            // Nome do arquivo gerado
            String fileName = request.getTemplateName().replace(".docx", "") + "_gerado.docx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(documentBytes.length)
                    .body(resource);
                    
        } catch (RuntimeException e) {
            log.error("Erro ao gerar documento do cliente: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Erro de IO ao gerar documento do cliente: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

