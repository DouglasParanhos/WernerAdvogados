package com.wa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wa.dto.DocumentContentDTO;
import com.wa.dto.DocumentContentResponseDTO;
import com.wa.model.Address;
import com.wa.model.Matriculation;
import com.wa.model.Person;
import com.wa.model.Process;
import com.wa.repository.PersonRepository;
import com.wa.repository.ProcessRepository;
import com.wa.util.DocumentSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordDocumentService {
    
    private final ProcessRepository processRepository;
    private final PersonRepository personRepository;
    private final DocumentTemplateService templateService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    
    /**
     * Gera um documento Word preenchendo o template com dados do processo
     */
    public byte[] generateDocument(Long processId, String templateName) throws IOException {
        // Buscar processo com todos os relacionamentos necessários
        Process process = processRepository.findByIdWithRelations(processId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado com ID: " + processId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new RuntimeException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar mapa de dados para substituição
        Map<String, String> dataMap = buildDataMap(process);
        
        // Processar template
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // Substituir placeholders em parágrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, dataMap);
            }
            
            // Substituir placeholders em tabelas (se houver)
            document.getTables().forEach(table -> 
                table.getRows().forEach(row -> 
                    row.getTableCells().forEach(cell -> 
                        cell.getParagraphs().forEach(paragraph -> 
                            replacePlaceholdersInParagraph(paragraph, dataMap)
                        )
                    )
                )
            );
            
            // Salvar documento em byte array
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Constrói mapa de dados para substituição de placeholders
     */
    private Map<String, String> buildDataMap(Process process) {
        Map<String, String> dataMap = new HashMap<>();
        
        Matriculation matriculation = process.getMatriculation();
        Person person = matriculation.getPerson();
        Address address = person.getAddress();
        
        // Dados pessoais
        dataMap.put("${nome}", safeString(person.getFullname()));
        dataMap.put("[NOME]", safeString(person.getFullname()));
        dataMap.put("NACIONALIDADE", safeString(person.getNacionalidade()));
        dataMap.put("PROFISSAO", safeString(person.getProfissao()));
        dataMap.put("ESTADOCIVIL", safeString(person.getEstadoCivil()));
        dataMap.put("IDENTIDADE", safeString(person.getRg()));
        dataMap.put("CPF", safeString(person.getCpf()));
        dataMap.put("IDFUNCIONAL", safeString(person.getIdFuncional()));
        dataMap.put("${cep}", safeString(address != null ? address.getCep() : ""));
        
        // Endereço completo
        String enderecoCompleto = "";
        if (address != null) {
            enderecoCompleto = safeString(address.getLogradouro()) + " " +
                              safeString(address.getCidade()) + " " +
                              safeString(address.getEstado());
        }
        dataMap.put("ENDERECO", enderecoCompleto.trim());
        dataMap.put("xxxxxxx", address != null ? safeString(address.getCidade()) : "");
        
        // Dados do processo
        dataMap.put("[PROCESSO]", safeString(process.getNumero()));
        dataMap.put("[VARA]", safeString(process.getVara()));
        dataMap.put("COMARCA", safeString(process.getComarca()));
        
        // Dados da matrícula
        dataMap.put("M4TRICUL4", safeString(matriculation.getNumero()));
        dataMap.put("C4RG0", safeString(matriculation.getCargo()));
        dataMap.put("R3F3R3NCI4", safeString(matriculation.getReferencia()));
        dataMap.put("N1V3L", safeString(matriculation.getNivelAtual()));
        dataMap.put("C4RG4H0R4RI4", safeString(matriculation.getTrienioAtual()));
        
        // Datas formatadas
        if (matriculation.getInicioErj() != null) {
            dataMap.put("DATAINICIO", formatDate(matriculation.getInicioErj()));
            dataMap.put("labelDataInicio", formatDate(matriculation.getInicioErj()));
            
            // Calcular datas futuras (5, 10, 15 anos depois)
            LocalDateTime inicioErj = matriculation.getInicioErj();
            dataMap.put("data5AnosDepois", formatDate(inicioErj.plusYears(5)));
            dataMap.put("data10AnosDepois", formatDate(inicioErj.plusYears(10)));
            dataMap.put("data15AnosDepois", formatDate(inicioErj.plusYears(15)));
        } else {
            dataMap.put("DATAINICIO", "");
            dataMap.put("labelDataInicio", "");
            dataMap.put("data5AnosDepois", "");
            dataMap.put("data10AnosDepois", "");
            dataMap.put("data15AnosDepois", "");
        }
        
        if (person.getDataNascimento() != null) {
            dataMap.put("DATANASCIMENTO", formatDate(person.getDataNascimento()));
        } else {
            dataMap.put("DATANASCIMENTO", "");
        }
        
        if (process.getDistribuidoEm() != null) {
            dataMap.put("DISTRIBUIDOEM", formatDate(process.getDistribuidoEm()));
        } else {
            dataMap.put("DISTRIBUIDOEM", "");
        }
        
        // Níveis calculados (baseado no código Java fornecido)
        try {
            int nivelInicial = Integer.parseInt(matriculation.getNivelAtual());
            dataMap.put("NIVELAUTOR", String.valueOf(nivelInicial));
            dataMap.put("nimais5", String.valueOf(nivelInicial + 1));
            dataMap.put("nimais10", String.valueOf(nivelInicial + 2));
            dataMap.put("nimais15", String.valueOf(nivelInicial + 3));
            dataMap.put("nimais20", String.valueOf(nivelInicial + 4));
        } catch (NumberFormatException e) {
            log.warn("Erro ao converter nível: {}", matriculation.getNivelAtual());
            dataMap.put("NIVELAUTOR", safeString(matriculation.getNivelAtual()));
            dataMap.put("nimais5", "");
            dataMap.put("nimais10", "");
            dataMap.put("nimais15", "");
            dataMap.put("nimais20", "");
        }
        
        // Valores monetários - usar valor efetivo (valorCorrigido ou valorOriginal)
        Double valorEfetivo = process.getValorCorrigido() != null ? process.getValorCorrigido() : process.getValorOriginal();
        if (valorEfetivo != null) {
            String valorFormatado = formatCurrency(valorEfetivo);
            dataMap.put("VALORBRUTO", valorFormatado);
            dataMap.put("VALOR", valorFormatado);
            
            // Calcular sucumbência (10% do valor bruto)
            double sucumbencia = valorEfetivo * 0.10;
            dataMap.put("SUCUMBENCIA", "R$ " + CURRENCY_FORMAT.format(sucumbencia));
        } else {
            dataMap.put("VALORBRUTO", "");
            dataMap.put("VALOR", "");
            dataMap.put("SUCUMBENCIA", "");
        }
        
        if (process.getPrevisaoHonorariosContratuais() != null) {
            dataMap.put("VALORRIOPREV", formatCurrency(process.getPrevisaoHonorariosContratuais()));
        } else {
            dataMap.put("VALORRIOPREV", "");
        }
        
        if (process.getPrevisaoHonorariosSucumbenciais() != null) {
            dataMap.put("VALORFINAL", formatCurrency(process.getPrevisaoHonorariosSucumbenciais()));
        } else {
            dataMap.put("VALORFINAL", "");
        }
        
        // Outros campos comuns
        dataMap.put("REFAUTOR", safeString(matriculation.getReferencia()));
        dataMap.put("CARGOAUTOR", safeString(matriculation.getCargo()));
        dataMap.put("#CH", safeString(matriculation.getTrienioAtual()));
        
        return dataMap;
    }
    
    /**
     * Substitui placeholders em um parágrafo
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> dataMap) {
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                String replacedText = text;
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    replacedText = replacedText.replace(entry.getKey(), entry.getValue());
                }
                run.setText(replacedText, 0);
            }
        }
    }
    
    /**
     * Formata data para padrão brasileiro
     */
    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formata valor monetário
     */
    private String formatCurrency(Double value) {
        if (value == null) {
            return "";
        }
        return "R$ " + CURRENCY_FORMAT.format(value);
    }
    
    /**
     * Gera um documento Word preenchendo o template com dados do cliente (pessoa)
     */
    public byte[] generateDocumentForClient(Long personId, String templateName) throws IOException {
        // Buscar pessoa com todos os relacionamentos necessários
        Person person = personRepository.findByIdWithRelations(personId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + personId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new RuntimeException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar mapa de dados para substituição
        Map<String, String> dataMap = buildDataMapFromPerson(person);
        
        // Processar template
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // Substituir placeholders em parágrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, dataMap);
            }
            
            // Substituir placeholders em tabelas (se houver)
            document.getTables().forEach(table -> 
                table.getRows().forEach(row -> 
                    row.getTableCells().forEach(cell -> 
                        cell.getParagraphs().forEach(paragraph -> 
                            replacePlaceholdersInParagraph(paragraph, dataMap)
                        )
                    )
                )
            );
            
            // Salvar documento em byte array
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Constrói mapa de dados para substituição de placeholders usando dados da pessoa
     */
    private Map<String, String> buildDataMapFromPerson(Person person) {
        Map<String, String> dataMap = new HashMap<>();
        
        Address address = person.getAddress();
        
        // Usar a primeira matrícula se houver múltiplas
        Matriculation matriculation = null;
        if (person.getMatriculations() != null && !person.getMatriculations().isEmpty()) {
            matriculation = person.getMatriculations().get(0);
        }
        
        // Dados pessoais
        dataMap.put("${nome}", safeString(person.getFullname()));
        dataMap.put("[NOME]", safeString(person.getFullname()));
        dataMap.put("NACIONALIDADE", safeString(person.getNacionalidade()));
        dataMap.put("PROFISSAO", safeString(person.getProfissao()));
        dataMap.put("ESTADOCIVIL", safeString(person.getEstadoCivil()));
        dataMap.put("IDENTIDADE", safeString(person.getRg()));
        dataMap.put("CPF", safeString(person.getCpf()));
        dataMap.put("IDFUNCIONAL", safeString(person.getIdFuncional()));
        dataMap.put("${cep}", safeString(address != null ? address.getCep() : ""));
        
        // Endereço completo
        String enderecoCompleto = "";
        if (address != null) {
            enderecoCompleto = safeString(address.getLogradouro()) + " " +
                              safeString(address.getCidade()) + " " +
                              safeString(address.getEstado());
        }
        dataMap.put("ENDERECO", enderecoCompleto.trim());
        dataMap.put("xxxxxxx", address != null ? safeString(address.getCidade()) : "");
        
        // Dados do processo (vazios para documentos de cliente)
        dataMap.put("[PROCESSO]", "");
        dataMap.put("[VARA]", "");
        dataMap.put("COMARCA", "");
        dataMap.put("DISTRIBUIDOEM", "");
        
        // Dados da matrícula (se houver)
        if (matriculation != null) {
            dataMap.put("M4TRICUL4", safeString(matriculation.getNumero()));
            dataMap.put("C4RG0", safeString(matriculation.getCargo()));
            dataMap.put("R3F3R3NCI4", safeString(matriculation.getReferencia()));
            dataMap.put("N1V3L", safeString(matriculation.getNivelAtual()));
            dataMap.put("C4RG4H0R4RI4", safeString(matriculation.getTrienioAtual()));
            dataMap.put("REFAUTOR", safeString(matriculation.getReferencia()));
            dataMap.put("CARGOAUTOR", safeString(matriculation.getCargo()));
            dataMap.put("#CH", safeString(matriculation.getTrienioAtual()));
            
            // Datas formatadas
            if (matriculation.getInicioErj() != null) {
                dataMap.put("DATAINICIO", formatDate(matriculation.getInicioErj()));
                dataMap.put("labelDataInicio", formatDate(matriculation.getInicioErj()));
                
                // Calcular datas futuras (5, 10, 15 anos depois)
                LocalDateTime inicioErj = matriculation.getInicioErj();
                dataMap.put("data5AnosDepois", formatDate(inicioErj.plusYears(5)));
                dataMap.put("data10AnosDepois", formatDate(inicioErj.plusYears(10)));
                dataMap.put("data15AnosDepois", formatDate(inicioErj.plusYears(15)));
            } else {
                dataMap.put("DATAINICIO", "");
                dataMap.put("labelDataInicio", "");
                dataMap.put("data5AnosDepois", "");
                dataMap.put("data10AnosDepois", "");
                dataMap.put("data15AnosDepois", "");
            }
            
            // Níveis calculados
            try {
                int nivelInicial = Integer.parseInt(matriculation.getNivelAtual());
                dataMap.put("NIVELAUTOR", String.valueOf(nivelInicial));
                dataMap.put("nimais5", String.valueOf(nivelInicial + 1));
                dataMap.put("nimais10", String.valueOf(nivelInicial + 2));
                dataMap.put("nimais15", String.valueOf(nivelInicial + 3));
                dataMap.put("nimais20", String.valueOf(nivelInicial + 4));
            } catch (NumberFormatException e) {
                log.warn("Erro ao converter nível: {}", matriculation.getNivelAtual());
                dataMap.put("NIVELAUTOR", safeString(matriculation.getNivelAtual()));
                dataMap.put("nimais5", "");
                dataMap.put("nimais10", "");
                dataMap.put("nimais15", "");
                dataMap.put("nimais20", "");
            }
        } else {
            // Se não há matrícula, preencher com valores vazios
            dataMap.put("M4TRICUL4", "");
            dataMap.put("C4RG0", "");
            dataMap.put("R3F3R3NCI4", "");
            dataMap.put("N1V3L", "");
            dataMap.put("C4RG4H0R4RI4", "");
            dataMap.put("REFAUTOR", "");
            dataMap.put("CARGOAUTOR", "");
            dataMap.put("#CH", "");
            dataMap.put("DATAINICIO", "");
            dataMap.put("labelDataInicio", "");
            dataMap.put("data5AnosDepois", "");
            dataMap.put("data10AnosDepois", "");
            dataMap.put("data15AnosDepois", "");
            dataMap.put("NIVELAUTOR", "");
            dataMap.put("nimais5", "");
            dataMap.put("nimais10", "");
            dataMap.put("nimais15", "");
            dataMap.put("nimais20", "");
        }
        
        // Data de nascimento
        if (person.getDataNascimento() != null) {
            dataMap.put("DATANASCIMENTO", formatDate(person.getDataNascimento()));
        } else {
            dataMap.put("DATANASCIMENTO", "");
        }
        
        // Valores monetários (vazios para documentos de cliente)
        dataMap.put("VALORBRUTO", "");
        dataMap.put("VALOR", "");
        dataMap.put("SUCUMBENCIA", "");
        dataMap.put("VALORRIOPREV", "");
        dataMap.put("VALORFINAL", "");
        
        return dataMap;
    }
    
    /**
     * Retorna string segura (não nula)
     */
    private String safeString(String value) {
        return value != null ? value : "";
    }
    
    /**
     * Extrai conteúdo do documento Word com dados do cliente substituídos
     * Retorna estrutura com texto e identificação de dados do cliente
     */
    public DocumentContentResponseDTO getDocumentContentForClient(Long personId, String templateName) throws IOException {
        // Validar templateName
        if (!DocumentSanitizer.validateTemplateName(templateName)) {
            throw new IllegalArgumentException("Nome de template inválido: " + templateName);
        }
        
        // Buscar pessoa com todos os relacionamentos necessários
        Person person = personRepository.findByIdWithRelations(personId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + personId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new RuntimeException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar mapa de dados para substituição (sanitizado)
        Map<String, String> dataMap = buildDataMapFromPerson(person);
        Map<String, String> sanitizedDataMap = new HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            sanitizedDataMap.put(entry.getKey(), DocumentSanitizer.sanitizeText(entry.getValue()));
        }
        
        // Criar mapa de valores originais (não sanitizados) para identificar dados do cliente
        Map<String, String> originalDataMap = buildDataMapFromPerson(person);
        Set<String> clientDataValues = new HashSet<>(originalDataMap.values());
        clientDataValues.remove(""); // Remover valores vazios
        
        // Extrair conteúdo do documento
        List<DocumentContentResponseDTO.ContentBlock> contentBlocks = new ArrayList<>();
        
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            
            // Processar parágrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String paragraphText = extractTextFromParagraph(paragraph, sanitizedDataMap);
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    // Identificar se contém dados do cliente
                    boolean isClientData = clientDataValues.stream()
                            .anyMatch(value -> paragraphText.contains(value));
                    
                    DocumentContentResponseDTO.ContentBlock block = new DocumentContentResponseDTO.ContentBlock();
                    block.setText(paragraphText);
                    block.setClientData(isClientData);
                    block.setFormatting(new HashMap<>());
                    contentBlocks.add(block);
                }
            }
            
            // Processar tabelas
            document.getTables().forEach(table -> 
                table.getRows().forEach(row -> 
                    row.getTableCells().forEach(cell -> 
                        cell.getParagraphs().forEach(paragraph -> {
                            String cellText = extractTextFromParagraph(paragraph, sanitizedDataMap);
                            if (cellText != null && !cellText.trim().isEmpty()) {
                                boolean isClientData = clientDataValues.stream()
                                        .anyMatch(value -> cellText.contains(value));
                                
                                DocumentContentResponseDTO.ContentBlock block = 
                                    new DocumentContentResponseDTO.ContentBlock();
                                block.setText(cellText);
                                block.setClientData(isClientData);
                                block.setFormatting(new HashMap<>());
                                contentBlocks.add(block);
                            }
                        })
                    )
                )
            );
        }
        
        // Criar mapa de dados do cliente (em maiúsculas para destacar)
        Map<String, String> clientDataMap = new HashMap<>();
        for (Map.Entry<String, String> entry : originalDataMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                clientDataMap.put(entry.getKey(), entry.getValue().toUpperCase());
            }
        }
        
        DocumentContentResponseDTO response = new DocumentContentResponseDTO();
        response.setTemplateName(templateName);
        response.setContent(contentBlocks);
        response.setClientData(clientDataMap);
        
        return response;
    }
    
    /**
     * Extrai texto de um parágrafo substituindo placeholders
     */
    private String extractTextFromParagraph(XWPFParagraph paragraph, Map<String, String> dataMap) {
        StringBuilder text = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null) {
                String replacedText = runText;
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    replacedText = replacedText.replace(entry.getKey(), entry.getValue());
                }
                text.append(replacedText);
            }
        }
        return text.toString();
    }
    
    /**
     * Gera documento Word a partir de conteúdo editado (Quill Delta)
     */
    public byte[] generateCustomDocumentForClient(Long personId, String templateName, DocumentContentDTO contentDTO) throws IOException {
        // Validar e sanitizar templateName
        if (!DocumentSanitizer.validateTemplateName(templateName)) {
            throw new IllegalArgumentException("Nome de template inválido: " + templateName);
        }
        
        // Validar e sanitizar Quill Delta
        if (!DocumentSanitizer.validateQuillDelta(contentDTO.getContent())) {
            throw new IllegalArgumentException("Conteúdo Quill Delta inválido");
        }
        
        JsonNode sanitizedDelta = DocumentSanitizer.sanitizeQuillDelta(contentDTO.getContent());
        
        // Verificar se pessoa existe (validação básica)
        if (!personRepository.existsById(personId)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + personId);
        }
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new RuntimeException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar novo documento baseado no template
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // Converter Quill Delta para conteúdo Word
            JsonNode ops = sanitizedDelta.get("ops");
            List<ParagraphContent> paragraphs = convertDeltaToParagraphsWithFormatting(ops);
            
            // Limpar parágrafos existentes e aplicar novo conteúdo
            List<XWPFParagraph> existingParagraphs = new ArrayList<>(document.getParagraphs());
            
            // Limpar todos os runs dos parágrafos existentes
            for (XWPFParagraph paragraph : existingParagraphs) {
                int runsCount = paragraph.getRuns().size();
                for (int i = runsCount - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
            }
            
            // Aplicar conteúdo aos parágrafos existentes
            int paragraphIndex = 0;
            for (XWPFParagraph paragraph : existingParagraphs) {
                if (paragraphIndex < paragraphs.size()) {
                    ParagraphContent paraContent = paragraphs.get(paragraphIndex);
                    XWPFRun run = paragraph.createRun();
                    run.setText(paraContent.getText());
                    if (paraContent.isBold()) run.setBold(true);
                    if (paraContent.isItalic()) run.setItalic(true);
                    if (paraContent.isUnderline()) {
                        run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                    }
                    paragraphIndex++;
                }
            }
            
            // Se há mais parágrafos no Delta do que no template, criar novos
            while (paragraphIndex < paragraphs.size()) {
                XWPFParagraph newParagraph = document.createParagraph();
                ParagraphContent paraContent = paragraphs.get(paragraphIndex);
                XWPFRun run = newParagraph.createRun();
                run.setText(paraContent.getText());
                if (paraContent.isBold()) run.setBold(true);
                if (paraContent.isItalic()) run.setItalic(true);
                if (paraContent.isUnderline()) {
                    run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                }
                paragraphIndex++;
            }
            
            // Salvar documento
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Classe auxiliar para armazenar conteúdo e formatação de parágrafo
     */
    private static class ParagraphContent {
        private String text;
        private boolean bold;
        private boolean italic;
        private boolean underline;
        
        public ParagraphContent(String text) {
            this.text = text;
        }
        
        public String getText() { return text; }
        public boolean isBold() { return bold; }
        public void setBold(boolean bold) { this.bold = bold; }
        public boolean isItalic() { return italic; }
        public void setItalic(boolean italic) { this.italic = italic; }
        public boolean isUnderline() { return underline; }
        public void setUnderline(boolean underline) { this.underline = underline; }
    }
    
    /**
     * Converte Quill Delta operations para lista de parágrafos com formatação
     */
    private List<ParagraphContent> convertDeltaToParagraphsWithFormatting(JsonNode ops) {
        List<ParagraphContent> paragraphs = new ArrayList<>();
        StringBuilder currentParagraph = new StringBuilder();
        boolean currentBold = false;
        boolean currentItalic = false;
        boolean currentUnderline = false;
        
        for (JsonNode op : ops) {
            if (op.has("insert")) {
                JsonNode insert = op.get("insert");
                if (insert.isTextual()) {
                    String text = DocumentSanitizer.escapeForWord(insert.asText());
                    
                    // Verificar atributos de formatação
                    if (op.has("attributes")) {
                        JsonNode attrs = op.get("attributes");
                        currentBold = attrs.has("bold") && attrs.get("bold").asBoolean();
                        currentItalic = attrs.has("italic") && attrs.get("italic").asBoolean();
                        currentUnderline = attrs.has("underline") && attrs.get("underline").asBoolean();
                    }
                    
                    // Quebras de linha indicam novo parágrafo
                    if (text.contains("\n")) {
                        String[] lines = text.split("\n", -1);
                        for (int i = 0; i < lines.length; i++) {
                            currentParagraph.append(lines[i]);
                            if (i < lines.length - 1) {
                                // Finalizar parágrafo atual e começar novo
                                ParagraphContent para = new ParagraphContent(currentParagraph.toString());
                                para.setBold(currentBold);
                                para.setItalic(currentItalic);
                                para.setUnderline(currentUnderline);
                                paragraphs.add(para);
                                currentParagraph = new StringBuilder();
                            }
                        }
                    } else {
                        currentParagraph.append(text);
                    }
                }
            } else if (op.has("delete")) {
                // Remover caracteres (simplificado - apenas reduzir tamanho)
                int deleteCount = op.get("delete").asInt();
                if (currentParagraph.length() >= deleteCount) {
                    currentParagraph.setLength(currentParagraph.length() - deleteCount);
                }
            }
            // retain não precisa ser processado aqui
        }
        
        // Adicionar último parágrafo se não estiver vazio
        if (currentParagraph.length() > 0) {
            ParagraphContent para = new ParagraphContent(currentParagraph.toString());
            para.setBold(currentBold);
            para.setItalic(currentItalic);
            para.setUnderline(currentUnderline);
            paragraphs.add(para);
        }
        
        return paragraphs;
    }
}

