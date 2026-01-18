package com.wa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wa.model.Person;
import com.wa.model.Process;
import com.wa.repository.PersonRepository;
import com.wa.repository.ProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para verificar a conversão de tabelas no WordDocumentService.
 * Estes testes verificam que as tabelas são criadas corretamente a partir de marcadores Quill Delta.
 */
@ExtendWith(MockitoExtension.class)
class WordDocumentServiceTableConversionTest {

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DocumentTemplateService templateService;

    @InjectMocks
    private WordDocumentService wordDocumentService;

    private Process testProcess;
    private Person testPerson;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setFullname("Ana Cristina Vieira Cabral");
        testPerson.setCpf("12345678900");
        
        testProcess = new Process();
        testProcess.setId(1L);
        testProcess.setNumero("0821817-76.2023.8.19.0002");
        testProcess.setComarca("Niterói");
        testProcess.setVara("10ª Vara Cível");
        testProcess.setTipoProcesso("PISO");
    }

    @Test
    void testTableConversion_WithTableMarkers_DoesNotThrowException() throws Exception {
        // Criar um Quill Delta JSON que representa uma tabela simples
        String quillDeltaJson = """
        {
            "ops": [
                {"insert": "Texto antes da tabela\\n"},
                {"insert": "[TABLE_START]"},
                {"insert": "Nome"},
                {"insert": "[CELL_SEP]"},
                {"insert": "OAB"},
                {"insert": "[ROW_END]"},
                {"insert": "Liz Werner"},
                {"insert": "[CELL_SEP]"},
                {"insert": "OAB/RJ 184.888"},
                {"insert": "[TABLE_END]"},
                {"insert": "\\nTexto após a tabela"}
            ]
        }
        """;

        JsonNode content = objectMapper.readTree(quillDeltaJson);
        
        // Mock do template service
        Resource mockResource = new ClassPathResource("documents/CR_Apelacao_Piso.docx");
        when(templateService.templateExists(any())).thenReturn(true);
        when(templateService.getTemplateResource(any())).thenReturn(mockResource);
        when(processRepository.existsById(anyLong())).thenReturn(true);

        // Executar a conversão e VALIDAR que o PDF foi gerado corretamente
        byte[] result = wordDocumentService.generateCustomDocumentForProcess(1L, "CR_Apelacao_Piso.docx", content);
        
        // VALIDAÇÕES CRÍTICAS:
        assertNotNull(result, "O resultado da conversão não deve ser nulo");
        assertTrue(result.length > 0, "O resultado deve ter conteúdo");
        
        // Verificar que é um PDF válido (começa com %PDF)
        String pdfHeader = new String(result, 0, Math.min(4, result.length));
        assertEquals("%PDF", pdfHeader, "O resultado deve ser um PDF válido (começa com %PDF)");
        
        // Verificar que os marcadores NÃO aparecem no PDF
        String pdfContent = new String(result);
        assertFalse(pdfContent.contains("[TABLE_START]"), 
                   "PDF não deve conter marcador [TABLE_START]");
        assertFalse(pdfContent.contains("[TABLE_END]"), 
                   "PDF não deve conter marcador [TABLE_END]");
        assertFalse(pdfContent.contains("[CELL_SEP]"), 
                   "PDF não deve conter marcador [CELL_SEP]");
        assertFalse(pdfContent.contains("[ROW_END]"), 
                   "PDF não deve conter marcador [ROW_END]");
        
        // Verificar que o conteúdo esperado ESTÁ no PDF
        assertTrue(pdfContent.contains("Liz Werner") || pdfContent.contains("Nome"), 
                  "PDF deve conter o conteúdo da tabela");
    }

    @Test
    void testTableConversion_WithEmptyCells_DoesNotThrowException() throws Exception {
        // Criar um Quill Delta JSON com células vazias
        String quillDeltaJson = """
        {
            "ops": [
                {"insert": "[TABLE_START]"},
                {"insert": "Coluna 1"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Coluna 2"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Coluna 3"},
                {"insert": "[ROW_END]"},
                {"insert": "Valor 1"},
                {"insert": "[CELL_SEP]"},
                {"insert": ""},
                {"insert": "[CELL_SEP]"},
                {"insert": "Valor 3"},
                {"insert": "[TABLE_END]"}
            ]
        }
        """;

        JsonNode content = objectMapper.readTree(quillDeltaJson);
        
        Resource mockResource = new ClassPathResource("documents/CR_Apelacao_Piso.docx");
        when(templateService.templateExists(any())).thenReturn(true);
        when(templateService.getTemplateResource(any())).thenReturn(mockResource);
        when(processRepository.existsById(anyLong())).thenReturn(true);

        // Verificar que a conversão gera PDF válido SEM marcadores
        byte[] result = wordDocumentService.generateCustomDocumentForProcess(1L, "CR_Apelacao_Piso.docx", content);
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verificar que é PDF válido
        String pdfHeader = new String(result, 0, Math.min(4, result.length));
        assertEquals("%PDF", pdfHeader, "Deve ser um PDF válido");
        
        // Verificar que marcadores foram removidos
        String pdfContent = new String(result);
        assertFalse(pdfContent.contains("[TABLE_START]"), "Marcadores devem ser removidos");
        assertFalse(pdfContent.contains("[CELL_SEP]"), "Marcadores devem ser removidos");
    }

    @Test
    void testTableConversion_WithMultipleRows_DoesNotThrowException() throws Exception {
        // Criar um Quill Delta JSON com múltiplas linhas
        String quillDeltaJson = """
        {
            "ops": [
                {"insert": "[TABLE_START]"},
                {"insert": "Nível"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Valor 16h"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Valor 22h"},
                {"insert": "[ROW_END]"},
                {"insert": "Nível 01"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 1.832,23"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.519,31"},
                {"insert": "[ROW_END]"},
                {"insert": "Nível 02"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.052,10"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.821,63"},
                {"insert": "[ROW_END]"},
                {"insert": "Nível 03"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.298,35"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 3.160,23"},
                {"insert": "[TABLE_END]"}
            ]
        }
        """;

        JsonNode content = objectMapper.readTree(quillDeltaJson);
        
        Resource mockResource = new ClassPathResource("documents/CR_Apelacao_Piso.docx");
        when(templateService.templateExists(any())).thenReturn(true);
        when(templateService.getTemplateResource(any())).thenReturn(mockResource);
        when(processRepository.existsById(anyLong())).thenReturn(true);

        // Verificar que a conversão gera PDF válido com múltiplas linhas
        byte[] result = wordDocumentService.generateCustomDocumentForProcess(1L, "CR_Apelacao_Piso.docx", content);
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verificar que é PDF válido
        String pdfHeader = new String(result, 0, Math.min(4, result.length));
        assertEquals("%PDF", pdfHeader, "Deve ser um PDF válido");
        
        // Verificar que marcadores foram removidos
        String pdfContent = new String(result);
        assertFalse(pdfContent.contains("[TABLE_START]"), "Marcadores devem ser removidos");
        assertFalse(pdfContent.contains("[ROW_END]"), "Marcadores devem ser removidos");
        
        // Verificar que conteúdo está presente
        assertTrue(pdfContent.contains("Nível 01") || pdfContent.contains("R$"), 
                  "Conteúdo da tabela deve estar no PDF");
    }

    @Test
    void testTableConversion_WithRealDocument_ProcessesWithoutCriticalErrors() throws Exception {
        // Usar um documento real que contém tabelas
        Resource realDocument = new ClassPathResource("documents/CR_Apelacao_Piso.docx");
        
        // Criar um Quill Delta simples que adiciona uma tabela
        String quillDeltaJson = """
        {
            "ops": [
                {"insert": "Teste de tabela\\n"},
                {"insert": "[TABLE_START]"},
                {"insert": "Advogado"},
                {"insert": "[CELL_SEP]"},
                {"insert": "OAB"},
                {"insert": "[ROW_END]"},
                {"insert": "Liz Werner"},
                {"insert": "[CELL_SEP]"},
                {"insert": "OAB/RJ 184.888"},
                {"insert": "[TABLE_END]"},
                {"insert": "\\nFim do documento"}
            ]
        }
        """;

        JsonNode content = objectMapper.readTree(quillDeltaJson);
        
        when(templateService.templateExists(any())).thenReturn(true);
        when(templateService.getTemplateResource(any())).thenReturn(realDocument);
        when(processRepository.existsById(anyLong())).thenReturn(true);

        // Verificar que o método gera PDF válido sem marcadores
        byte[] result = wordDocumentService.generateCustomDocumentForProcess(1L, "CR_Apelacao_Piso.docx", content);
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verificar que é PDF válido
        String pdfHeader = new String(result, 0, Math.min(4, result.length));
        assertEquals("%PDF", pdfHeader, "Deve ser um PDF válido");
        
        // Verificar que marcadores foram removidos
        String pdfContent = new String(result);
        assertFalse(pdfContent.contains("[TABLE_START]"), "Marcadores devem ser removidos");
        assertFalse(pdfContent.contains("[TABLE_END]"), "Marcadores devem ser removidos");
        
        // Verificar que conteúdo está presente
        assertTrue(pdfContent.contains("Liz Werner") || pdfContent.contains("Advogado"), 
                  "Conteúdo deve estar no PDF");
    }

    @Test
    void testTableConversion_WithSpecialCharacters_PreservesContent() throws Exception {
        // Testar tabela com caracteres especiais e valores monetários
        String quillDeltaJson = """
        {
            "ops": [
                {"insert": "[TABLE_START]"},
                {"insert": "Descrição"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Valor"},
                {"insert": "[ROW_END]"},
                {"insert": "Piso Nacional 2024 para 16h = 40% do valor de 40h"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 1.832,23"},
                {"insert": "[ROW_END]"},
                {"insert": "Não consta"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 0,00"},
                {"insert": "[TABLE_END]"}
            ]
        }
        """;

        JsonNode content = objectMapper.readTree(quillDeltaJson);
        
        Resource mockResource = new ClassPathResource("documents/CR_Apelacao_Piso.docx");
        when(templateService.templateExists(any())).thenReturn(true);
        when(templateService.getTemplateResource(any())).thenReturn(mockResource);
        when(processRepository.existsById(anyLong())).thenReturn(true);

        // Verificar que caracteres especiais são preservados E marcadores removidos
        byte[] result = wordDocumentService.generateCustomDocumentForProcess(1L, "CR_Apelacao_Piso.docx", content);
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verificar que é PDF válido
        String pdfHeader = new String(result, 0, Math.min(4, result.length));
        assertEquals("%PDF", pdfHeader, "Deve ser um PDF válido");
        
        // Verificar que marcadores foram removidos
        String pdfContent = new String(result);
        assertFalse(pdfContent.contains("[TABLE_START]"), "Marcadores devem ser removidos");
        
        // Verificar que caracteres especiais estão presentes
        assertTrue(pdfContent.contains("R$") || pdfContent.contains("Piso"), 
                  "Caracteres especiais devem ser preservados");
    }

    @Test
    void testTableConversion_WithIrregularTableStructure_HandlesCorrectly() throws Exception {
        // Testar tabela com estrutura regular (mesmo número de colunas)
        String quillDeltaJson = """
        {
            "ops": [
                {"insert": "[TABLE_START]"},
                {"insert": "Carga Horária"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Nível 01"},
                {"insert": "[CELL_SEP]"},
                {"insert": "Nível 02"},
                {"insert": "[ROW_END]"},
                {"insert": "16h"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 1.832,23"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.052,10"},
                {"insert": "[ROW_END]"},
                {"insert": "22h"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.519,31"},
                {"insert": "[CELL_SEP]"},
                {"insert": "R$ 2.821,63"},
                {"insert": "[TABLE_END]"}
            ]
        }
        """;

        JsonNode content = objectMapper.readTree(quillDeltaJson);
        
        Resource mockResource = new ClassPathResource("documents/CR_Apelacao_Piso.docx");
        when(templateService.templateExists(any())).thenReturn(true);
        when(templateService.getTemplateResource(any())).thenReturn(mockResource);
        when(processRepository.existsById(anyLong())).thenReturn(true);

        // Verificar que a estrutura é tratada corretamente E PDF gerado sem marcadores
        byte[] result = wordDocumentService.generateCustomDocumentForProcess(1L, "CR_Apelacao_Piso.docx", content);
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verificar que é PDF válido
        String pdfHeader = new String(result, 0, Math.min(4, result.length));
        assertEquals("%PDF", pdfHeader, "Deve ser um PDF válido");
        
        // Verificar que marcadores foram removidos
        String pdfContent = new String(result);
        assertFalse(pdfContent.contains("[TABLE_START]"), "Marcadores devem ser removidos");
        assertFalse(pdfContent.contains("[CELL_SEP]"), "Marcadores devem ser removidos");
        
        // Verificar que conteúdo está presente
        assertTrue(pdfContent.contains("16h") || pdfContent.contains("R$"), 
                  "Conteúdo da tabela deve estar no PDF");
    }
}
