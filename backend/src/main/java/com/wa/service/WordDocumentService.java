package com.wa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wa.dto.DocumentContentDTO;
import com.wa.dto.DocumentContentResponseDTO;
import com.wa.exception.DocumentTemplateNotFoundException;
import com.wa.exception.PersonNotFoundException;
import com.wa.exception.ProcessNotFoundException;
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
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

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
                .orElseThrow(() -> new ProcessNotFoundException(processId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new DocumentTemplateNotFoundException("Template não encontrado: " + templateName);
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
     * e converte para PDF usando Docx4J conforme artigo javathinking.com
     */
    public byte[] generateDocumentForClient(Long personId, String templateName) throws IOException {
        // Buscar pessoa com todos os relacionamentos necessários
        Person person = personRepository.findByIdWithRelations(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new DocumentTemplateNotFoundException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar mapa de dados para substituição
        Map<String, String> dataMap = buildDataMapFromPerson(person);
        
        // Processar template e gerar DOCX
        byte[] docxBytes;
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
            
            // Salvar documento DOCX em byte array
            document.write(outputStream);
            docxBytes = outputStream.toByteArray();
        }
        
        // Converter DOCX para PDF usando Docx4J conforme artigo
        try {
            log.info("Convertendo DOCX para PDF usando Docx4J - Cliente ID: {}", personId);
            return convertDocxToPdfWithDocx4J(docxBytes);
        } catch (Exception e) {
            log.error("Erro ao converter DOCX para PDF: {}", e.getMessage(), e);
            throw new IOException("Erro ao converter documento para PDF: " + e.getMessage(), e);
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
                .orElseThrow(() -> new PersonNotFoundException(personId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new DocumentTemplateNotFoundException("Template não encontrado: " + templateName);
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
                List<RunContent> runs = extractRunsFromParagraph(paragraph, sanitizedDataMap);
                if (!runs.isEmpty()) {
                    // Combinar texto de todos os runs para verificar se contém dados do cliente
                    String paragraphText = runs.stream()
                            .map(RunContent::getText)
                            .reduce("", (a, b) -> a + b);
                    
                    if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                        // Identificar se contém dados do cliente
                        boolean isClientData = clientDataValues.stream()
                                .anyMatch(value -> paragraphText.contains(value));
                        
                        // Criar um bloco para cada run para preservar formatação granular
                        for (RunContent run : runs) {
                            DocumentContentResponseDTO.ContentBlock block = new DocumentContentResponseDTO.ContentBlock();
                            block.setText(run.getText());
                            block.setClientData(isClientData);
                            
                            // Adicionar formatação ao bloco
                            Map<String, Object> formatting = new HashMap<>();
                            if (run.isBold()) formatting.put("bold", true);
                            if (run.isItalic()) formatting.put("italic", true);
                            if (run.isUnderline()) formatting.put("underline", true);
                            block.setFormatting(formatting);
                            
                            contentBlocks.add(block);
                        }
                        
                        // Adicionar quebra de linha após o parágrafo
                        DocumentContentResponseDTO.ContentBlock lineBreak = 
                            new DocumentContentResponseDTO.ContentBlock();
                        lineBreak.setText("\n");
                        lineBreak.setClientData(false);
                        lineBreak.setFormatting(new HashMap<>());
                        contentBlocks.add(lineBreak);
                    }
                }
            }
            
            // Processar tabelas - marcar como tabela para preservar estrutura
            document.getTables().forEach(table -> {
                // Adicionar marcador de início de tabela
                DocumentContentResponseDTO.ContentBlock tableStart = new DocumentContentResponseDTO.ContentBlock();
                tableStart.setText("[TABLE_START]");
                tableStart.setClientData(false);
                Map<String, Object> tableFormatting = new HashMap<>();
                tableFormatting.put("isTable", true);
                tableStart.setFormatting(tableFormatting);
                contentBlocks.add(tableStart);
                
                table.getRows().forEach(row -> {
                    int cellCount = 0;
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        List<RunContent> cellRuns = new ArrayList<>();
                        cell.getParagraphs().forEach(paragraph -> {
                            cellRuns.addAll(extractRunsFromParagraph(paragraph, sanitizedDataMap));
                        });
                        
                        if (!cellRuns.isEmpty()) {
                            String cellText = cellRuns.stream()
                                    .map(RunContent::getText)
                                    .reduce("", (a, b) -> a + b);
                            
                            if (cellText != null && !cellText.trim().isEmpty()) {
                                boolean isClientData = clientDataValues.stream()
                                        .anyMatch(value -> cellText.contains(value));
                                
                                // Criar bloco para cada run da célula
                                for (RunContent run : cellRuns) {
                                    DocumentContentResponseDTO.ContentBlock block = 
                                        new DocumentContentResponseDTO.ContentBlock();
                                    block.setText(run.getText());
                                    block.setClientData(isClientData);
                                    
                                    Map<String, Object> formatting = new HashMap<>();
                                    formatting.put("isTableCell", true);
                                    if (run.isBold()) formatting.put("bold", true);
                                    if (run.isItalic()) formatting.put("italic", true);
                                    if (run.isUnderline()) formatting.put("underline", true);
                                    block.setFormatting(formatting);
                                    
                                    contentBlocks.add(block);
                                }
                            }
                        }
                        
                        cellCount++;
                        // Adicionar separador de célula (exceto após a última célula da linha)
                        if (cellCount < row.getTableCells().size()) {
                            DocumentContentResponseDTO.ContentBlock cellSeparator = 
                                new DocumentContentResponseDTO.ContentBlock();
                            cellSeparator.setText("[CELL_SEP]");
                            cellSeparator.setClientData(false);
                            Map<String, Object> sepFormatting = new HashMap<>();
                            sepFormatting.put("isTableCell", true);
                            cellSeparator.setFormatting(sepFormatting);
                            contentBlocks.add(cellSeparator);
                        }
                    }
                    
                    // Adicionar separador de linha após cada linha (exceto a última)
                    if (row != table.getRows().get(table.getRows().size() - 1)) {
                        DocumentContentResponseDTO.ContentBlock rowSeparator = 
                            new DocumentContentResponseDTO.ContentBlock();
                        rowSeparator.setText("[ROW_END]");
                        rowSeparator.setClientData(false);
                        Map<String, Object> rowFormatting = new HashMap<>();
                        rowFormatting.put("isTable", true);
                        rowSeparator.setFormatting(rowFormatting);
                        contentBlocks.add(rowSeparator);
                    }
                });
                
                // Adicionar marcador de fim de tabela
                DocumentContentResponseDTO.ContentBlock tableEnd = new DocumentContentResponseDTO.ContentBlock();
                tableEnd.setText("[TABLE_END]\n");
                tableEnd.setClientData(false);
                Map<String, Object> endFormatting = new HashMap<>();
                endFormatting.put("isTable", true);
                tableEnd.setFormatting(endFormatting);
                contentBlocks.add(tableEnd);
            });
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
     * Extrai runs de um parágrafo com formatação, substituindo placeholders
     */
    private List<RunContent> extractRunsFromParagraph(XWPFParagraph paragraph, Map<String, String> dataMap) {
        List<RunContent> runs = new ArrayList<>();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null && !runText.trim().isEmpty()) {
                String replacedText = runText;
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    replacedText = replacedText.replace(entry.getKey(), entry.getValue());
                }
                
                RunContent runContent = new RunContent(replacedText);
                runContent.setBold(run.isBold());
                runContent.setItalic(run.isItalic());
                if (run.getUnderline() != null && run.getUnderline() != org.apache.poi.xwpf.usermodel.UnderlinePatterns.NONE) {
                    runContent.setUnderline(true);
                }
                runs.add(runContent);
            }
        }
        return runs;
    }
    
    /**
     * Gera documento Word a partir de conteúdo editado (Quill Delta)
     * Retorna PDF para documentos editados de clientes
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
            throw new PersonNotFoundException(personId);
        }
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new DocumentTemplateNotFoundException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar novo documento baseado no template
        byte[] docxBytes;
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // Converter Quill Delta para conteúdo Word (parágrafos e tabelas)
            JsonNode ops = sanitizedDelta.get("ops");
            DocumentContent docContent = convertDeltaToDocumentContent(ops);
            List<ParagraphContent> paragraphs = docContent.getParagraphs();
            List<TableContent> tables = docContent.getTables();
            
            // IMPORTANTE: Preservar tabelas do template original e aplicar conteúdo editado
            
            // Limpar apenas parágrafos existentes (não tabelas) e aplicar novo conteúdo
            List<XWPFParagraph> existingParagraphs = new ArrayList<>(document.getParagraphs());
            
            // Limpar todos os runs dos parágrafos existentes
            for (XWPFParagraph paragraph : existingParagraphs) {
                int runsCount = paragraph.getRuns().size();
                for (int i = runsCount - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
            }
            
            // Aplicar conteúdo aos parágrafos existentes com múltiplos runs
            int paragraphIndex = 0;
            for (XWPFParagraph paragraph : existingParagraphs) {
                if (paragraphIndex < paragraphs.size()) {
                    ParagraphContent paraContent = paragraphs.get(paragraphIndex);
                    
                    // Criar múltiplos runs para preservar formatação granular
                    for (RunContent runContent : paraContent.getRuns()) {
                    XWPFRun run = paragraph.createRun();
                    // REMOVER MARCADORES DE TABELA (verificação de segurança)
                    String text = runContent.getText();
                    if (text != null) {
                        // Múltiplas passadas para garantir remoção completa
                        text = removeTableMarkers(text);
                        text = removeTableMarkers(text); // Segunda passada
                        // Verificação final: se ainda contém marcadores, usar método agressivo
                        if (text.contains("[TABLE_START]") || text.contains("[TABLE_END]") || 
                            text.contains("[ROW_END]") || text.contains("[CELL_SEP]")) {
                            String[] parts = text.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                            text = String.join("", parts);
                        }
                    } else {
                        text = "";
                    }
                    run.setText(text);
                        if (runContent.isBold()) run.setBold(true);
                        if (runContent.isItalic()) run.setItalic(true);
                        if (runContent.isUnderline()) {
                            run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                        }
                    }
                    paragraphIndex++;
                }
            }
            
            // Se há mais parágrafos no Delta do que no template, criar novos
            while (paragraphIndex < paragraphs.size()) {
                XWPFParagraph newParagraph = document.createParagraph();
                ParagraphContent paraContent = paragraphs.get(paragraphIndex);
                
                // Criar múltiplos runs para preservar formatação granular
                for (RunContent runContent : paraContent.getRuns()) {
                    XWPFRun run = newParagraph.createRun();
                    // REMOVER MARCADORES DE TABELA (verificação de segurança)
                    String text = runContent.getText();
                    if (text != null) {
                        // Múltiplas passadas para garantir remoção completa
                        text = removeTableMarkers(text);
                        text = removeTableMarkers(text); // Segunda passada
                        // Verificação final: se ainda contém marcadores, usar método agressivo
                        if (text.contains("[TABLE_START]") || text.contains("[TABLE_END]") || 
                            text.contains("[ROW_END]") || text.contains("[CELL_SEP]")) {
                            String[] parts = text.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                            text = String.join("", parts);
                        }
                    } else {
                        text = "";
                    }
                    run.setText(text);
                    if (runContent.isBold()) run.setBold(true);
                    if (runContent.isItalic()) run.setItalic(true);
                    if (runContent.isUnderline()) {
                        run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                    }
                }
                paragraphIndex++;
            }
            
            // Aplicar conteúdo editado às tabelas do template original e criar novas se necessário
            List<org.apache.poi.xwpf.usermodel.XWPFTable> existingTables = document.getTables();
            int tableIndex = 0;
            for (TableContent tableContent : tables) {
                org.apache.poi.xwpf.usermodel.XWPFTable table;
                List<List<List<RunContent>>> rows = tableContent.getRows();
                
                // Se existe tabela no template, usar ela; caso contrário, criar nova
                if (tableIndex < existingTables.size()) {
                    table = existingTables.get(tableIndex);
                } else {
                    // Criar nova tabela - primeiro criar um parágrafo e então a tabela
                    document.createParagraph();
                    // Determinar número máximo de colunas em todas as linhas
                    int maxCols = rows.stream()
                        .mapToInt(List::size)
                        .max()
                        .orElse(1);
                    table = document.createTable(1, maxCols);
                    // Remover a linha inicial que será substituída pelo conteúdo
                    if (!table.getRows().isEmpty()) {
                        table.removeRow(0);
                    }
                    // Aplicar formatação básica à tabela
                    applyTableFormatting(table);
                }
                
                // Processar todas as linhas do conteúdo editado
                List<org.apache.poi.xwpf.usermodel.XWPFTableRow> tableRows = table.getRows();
                
                for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                    List<List<RunContent>> editedRow = rows.get(rowIndex);
                    org.apache.poi.xwpf.usermodel.XWPFTableRow tableRow;
                    
                    // Se a linha existe na tabela, usar; caso contrário, criar
                    if (rowIndex < tableRows.size()) {
                        tableRow = tableRows.get(rowIndex);
                    } else {
                        tableRow = table.createRow();
                    }
                    
                    // Garantir que a linha tenha células suficientes
                    List<org.apache.poi.xwpf.usermodel.XWPFTableCell> cells = tableRow.getTableCells();
                    while (cells.size() < editedRow.size()) {
                        tableRow.createCell();
                        cells = tableRow.getTableCells();
                    }
                    
                    // Processar todas as células da linha editada
                    for (int cellIndex = 0; cellIndex < editedRow.size(); cellIndex++) {
                        List<RunContent> editedCell = editedRow.get(cellIndex);
                        org.apache.poi.xwpf.usermodel.XWPFTableCell cell = cells.get(cellIndex);
                        
                        // Aplicar conteúdo editado à célula com suporte a múltiplos parágrafos
                        applyCellContent(cell, editedCell);
                    }
                }
                
                // Remover linhas extras da tabela se houver mais linhas no template do que no conteúdo editado
                tableRows = table.getRows();
                while (tableRows.size() > rows.size()) {
                    table.removeRow(tableRows.size() - 1);
                    tableRows = table.getRows();
                }
                
                // Aplicar formatação final à tabela (bordas, larguras, etc.)
                applyTableFormatting(table);
                
                tableIndex++;
            }
            
            // VERIFICAÇÃO FINAL: Remover marcadores de tabela de todo o documento antes de salvar
            removeTableMarkersFromDocument(document);
            
            // DEBUG CRÍTICO: Validar estrutura do documento antes de salvar
            int numParagraphs = document.getParagraphs().size();
            int numTables = document.getTables().size();
            log.info("=== VALIDAÇÃO DOCX ANTES DE SALVAR ===");
            log.info("Parágrafos no documento: {}", numParagraphs);
            log.info("Tabelas no documento: {}", numTables);
            
            // Validar que as tabelas foram criadas corretamente
            for (int i = 0; i < numTables; i++) {
                org.apache.poi.xwpf.usermodel.XWPFTable table = document.getTables().get(i);
                int numRows = table.getRows().size();
                int numCols = numRows > 0 ? table.getRow(0).getTableCells().size() : 0;
                log.info("Tabela {} - Linhas: {}, Colunas (primeira linha): {}", i, numRows, numCols);
                
                // Validar conteúdo das primeiras células
                if (numRows > 0 && numCols > 0) {
                    try {
                        String firstCellText = table.getRow(0).getCell(0).getText();
                        log.info("Tabela {} - Primeira célula: '{}' (tamanho: {})", 
                                i, firstCellText.substring(0, Math.min(50, firstCellText.length())), 
                                firstCellText.length());
                    } catch (Exception e) {
                        log.warn("Erro ao ler primeira célula da tabela {}: {}", i, e.getMessage());
                    }
                }
            }
            
            // Salvar documento DOCX
            document.write(outputStream);
            docxBytes = outputStream.toByteArray();
            
            log.info("DOCX gerado com sucesso - Tamanho: {} bytes", docxBytes.length);
        }
        
        // Converter DOCX para PDF
        try {
            log.info("=== INICIANDO CONVERSÃO DOCX PARA PDF ===");
            byte[] pdfBytes = convertDocxToPdf(docxBytes);
            log.info("PDF gerado com sucesso - Tamanho: {} bytes", pdfBytes.length);
            return pdfBytes;
        } catch (Exception e) {
            log.error("=== ERRO NA CONVERSÃO DOCX PARA PDF ===");
            log.error("Mensagem: {}", e.getMessage());
            log.error("DOCX gerado tinha {} bytes", docxBytes.length);
            log.error("Stack trace completo:", e);
            
            // IMPORTANTE: Se a conversão falhar, fornecer informações detalhadas
            throw new IOException("Erro ao converter documento para PDF. " +
                                "DOCX foi gerado com sucesso (" + docxBytes.length + " bytes). " +
                                "O erro ocorreu durante a conversão: " + e.getMessage(), e);
        }
    }
    
    /**
     * Aplica formatação completa a uma tabela (bordas, larguras, etc.)
     */
    private void applyTableFormatting(XWPFTable table) {
        try {
            CTTbl cttbl = table.getCTTbl();
            CTTblPr tblPr = cttbl.getTblPr();
            if (tblPr == null) {
                tblPr = cttbl.addNewTblPr();
            }
            
            // Configurar largura da tabela (100% da página)
            CTTblWidth tblWidth = tblPr.getTblW();
            if (tblWidth == null) {
                tblWidth = tblPr.addNewTblW();
            }
            tblWidth.setType(STTblWidth.PCT);
            tblWidth.setW(BigInteger.valueOf(10000)); // 100% em twips (1/20 de ponto)
            
            // Aplicar bordas à tabela - SEMPRE criar novas para garantir que sejam aplicadas
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders borders = tblPr.getTblBorders();
            if (borders == null) {
                borders = tblPr.addNewTblBorders();
            }
            
            // Remover bordas existentes e criar novas para garantir aplicação correta
            if (borders.getTop() != null) borders.unsetTop();
            if (borders.getLeft() != null) borders.unsetLeft();
            if (borders.getBottom() != null) borders.unsetBottom();
            if (borders.getRight() != null) borders.unsetRight();
            if (borders.getInsideH() != null) borders.unsetInsideH();
            if (borders.getInsideV() != null) borders.unsetInsideV();
            
            // Configurar bordas (top, left, bottom, right, insideH, insideV) - sempre criar novas
            setBorder(borders.addNewTop(), STBorder.SINGLE, 4, "000000");
            setBorder(borders.addNewLeft(), STBorder.SINGLE, 4, "000000");
            setBorder(borders.addNewBottom(), STBorder.SINGLE, 4, "000000");
            setBorder(borders.addNewRight(), STBorder.SINGLE, 4, "000000");
            setBorder(borders.addNewInsideH(), STBorder.SINGLE, 4, "000000");
            setBorder(borders.addNewInsideV(), STBorder.SINGLE, 4, "000000");
            
            // Calcular número de colunas na tabela
            int numCols = 0;
            if (!table.getRows().isEmpty()) {
                numCols = table.getRow(0).getTableCells().size();
            }
            
            // Distribuir larguras das colunas igualmente
            if (numCols > 0) {
                // Largura total aproximada em DXA (1 DXA = 1/20 de ponto)
                // Assumindo largura de página de ~8.5 polegadas = 12240 DXA
                long totalWidth = 12240; // Largura aproximada da página em DXA
                long colWidth = totalWidth / numCols;
                
                // Aplicar formatação às células com larguras distribuídas
                for (XWPFTableRow row : table.getRows()) {
                    int colIndex = 0;
                    for (XWPFTableCell cell : row.getTableCells()) {
                        applyCellFormatting(cell, colWidth, colIndex);
                        colIndex++;
                    }
                }
            } else {
                // Aplicar formatação básica se não houver colunas
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        applyCellFormatting(cell, 0, 0);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao aplicar formatação à tabela: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Configura uma borda de tabela - sempre aplica, mesmo se já existir
     */
    private void setBorder(CTBorder border, STBorder.Enum style, int size, String color) {
        if (border != null) {
            border.setVal(style);
            border.setSz(BigInteger.valueOf(size * 2)); // size em half-points
            border.setColor(color);
        }
    }
    
    /**
     * Aplica formatação completa a uma célula
     * @param cell A célula a ser formatada
     * @param width Largura da célula em DXA (0 para usar largura automática)
     * @param colIndex Índice da coluna (para ajustes específicos)
     */
    private void applyCellFormatting(XWPFTableCell cell, long width, int colIndex) {
        try {
            // Configurar propriedades da célula
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }
            
            // Configurar largura da célula
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth cellWidth = tcPr.getTcW();
            if (cellWidth == null) {
                cellWidth = tcPr.addNewTcW();
            }
            if (width > 0) {
                cellWidth.setType(STTblWidth.DXA);
                cellWidth.setW(BigInteger.valueOf(width));
            } else {
                cellWidth.setType(STTblWidth.AUTO);
            }
            
            // Configurar espaçamento interno da célula (padding)
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcMar cellMar = tcPr.getTcMar();
            if (cellMar == null) {
                cellMar = tcPr.addNewTcMar();
            }
            
            // Padding superior - sempre aplicar
            if (cellMar.getTop() != null) {
                cellMar.getTop().setType(STTblWidth.DXA);
                cellMar.getTop().setW(BigInteger.valueOf(144));
            } else {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth topMar = cellMar.addNewTop();
                topMar.setType(STTblWidth.DXA);
                topMar.setW(BigInteger.valueOf(144)); // ~7.2 pontos
            }
            
            // Padding inferior - sempre aplicar
            if (cellMar.getBottom() != null) {
                cellMar.getBottom().setType(STTblWidth.DXA);
                cellMar.getBottom().setW(BigInteger.valueOf(144));
            } else {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth bottomMar = cellMar.addNewBottom();
                bottomMar.setType(STTblWidth.DXA);
                bottomMar.setW(BigInteger.valueOf(144));
            }
            
            // Padding esquerdo - sempre aplicar
            if (cellMar.getLeft() != null) {
                cellMar.getLeft().setType(STTblWidth.DXA);
                cellMar.getLeft().setW(BigInteger.valueOf(144));
            } else {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth leftMar = cellMar.addNewLeft();
                leftMar.setType(STTblWidth.DXA);
                leftMar.setW(BigInteger.valueOf(144));
            }
            
            // Padding direito - sempre aplicar
            if (cellMar.getRight() != null) {
                cellMar.getRight().setType(STTblWidth.DXA);
                cellMar.getRight().setW(BigInteger.valueOf(144));
            } else {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth rightMar = cellMar.addNewRight();
                rightMar.setType(STTblWidth.DXA);
                rightMar.setW(BigInteger.valueOf(144));
            }
            
            // Configurar bordas da célula individualmente para garantir que apareçam no PDF
            CTTcBorders cellBorders = tcPr.getTcBorders();
            if (cellBorders == null) {
                cellBorders = tcPr.addNewTcBorders();
            }
            
            // Remover bordas existentes e criar novas
            if (cellBorders.getTop() != null) cellBorders.unsetTop();
            if (cellBorders.getLeft() != null) cellBorders.unsetLeft();
            if (cellBorders.getBottom() != null) cellBorders.unsetBottom();
            if (cellBorders.getRight() != null) cellBorders.unsetRight();
            
            // Aplicar bordas nas células (mesmas configurações da tabela)
            setBorder(cellBorders.addNewTop(), STBorder.SINGLE, 4, "000000");
            setBorder(cellBorders.addNewLeft(), STBorder.SINGLE, 4, "000000");
            setBorder(cellBorders.addNewBottom(), STBorder.SINGLE, 4, "000000");
            setBorder(cellBorders.addNewRight(), STBorder.SINGLE, 4, "000000");
            
            // Configurar alinhamento vertical (centro)
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc vAlign = tcPr.getVAlign();
            if (vAlign == null) {
                vAlign = tcPr.addNewVAlign();
            }
            vAlign.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc.CENTER);
            
            // Configurar alinhamento horizontal do parágrafo baseado no conteúdo
            for (XWPFParagraph para : cell.getParagraphs()) {
                if (para.getCTP() != null) {
                    CTP ctp = para.getCTP();
                    CTPPr pPr = ctp.getPPr();
                    if (pPr == null) {
                        pPr = ctp.addNewPPr();
                    }
                    CTJc jc = pPr.getJc();
                    if (jc == null) {
                        jc = pPr.addNewJc();
                    }
                    
                    // Detectar se o conteúdo é um valor monetário e alinhar à direita
                    String cellText = para.getText();
                    boolean isMonetary = false;
                    if (cellText != null && !cellText.trim().isEmpty()) {
                        String trimmed = cellText.trim();
                        // Detectar valores monetários: R$ seguido de números, ou números com vírgula/ponto
                        isMonetary = trimmed.matches("^R\\$\\s?[\\d.,]+") || 
                                    (trimmed.matches("^[\\d.,]+") && (trimmed.contains(",") || trimmed.contains("."))) ||
                                    trimmed.matches("^[\\d]+,[\\d]{2}$"); // Formato: 1234,56
                    }
                    
                    if (isMonetary) {
                        jc.setVal(STJc.RIGHT);
                    } else {
                        jc.setVal(STJc.LEFT);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao aplicar formatação à célula: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Remove marcadores de tabela de um texto (verificação de segurança)
     * Remove todas as ocorrências dos marcadores, mesmo se houver espaços ou caracteres especiais
     * Executa múltiplas passadas para garantir remoção completa
     * MÉTODO CRÍTICO: Deve remover TODOS os marcadores, sem exceção
     * 
     * VERSÃO ULTRA-AGRESSIVA: Usa múltiplos métodos em sequência para garantir remoção 100%
     */
    private String removeTableMarkers(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        String result = text;
        
        // MÉTODO 1: Dividir por marcadores e juntar apenas as partes válidas (mais eficiente)
        String[] parts = result.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
        result = String.join("", parts);
        
        // MÉTODO 2: Replace direto (caso o split não tenha capturado tudo)
        result = result.replace("[TABLE_START]", "")
                       .replace("[TABLE_END]", "")
                       .replace("[ROW_END]", "")
                       .replace("[CELL_SEP]", "");
        
        // MÉTODO 3: Replace com regex (caso haja variações)
        result = result.replaceAll("\\[TABLE_START\\]", "")
                       .replaceAll("\\[TABLE_END\\]", "")
                       .replaceAll("\\[ROW_END\\]", "")
                       .replaceAll("\\[CELL_SEP\\]", "");
        
        // MÉTODO 4: Remover padrões mesmo sem colchetes completos (último recurso)
        if (result.contains("TABLE_START") || result.contains("TABLE_END") || 
            result.contains("ROW_END") || result.contains("CELL_SEP")) {
            result = result.replaceAll("\\[?TABLE_START\\]?", "")
                          .replaceAll("\\[?TABLE_END\\]?", "")
                          .replaceAll("\\[?ROW_END\\]?", "")
                          .replaceAll("\\[?CELL_SEP\\]?", "");
        }
        
        // MÉTODO 5: Verificação final - se ainda contém, fazer split novamente
        if (result.contains("[TABLE_START]") || result.contains("[TABLE_END]") || 
            result.contains("[ROW_END]") || result.contains("[CELL_SEP]")) {
            parts = result.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
            result = String.join("", parts);
        }
        
        return result;
    }
    
    /**
     * Remove marcadores de tabela de todo o documento Word (verificação final)
     * Percorre todos os parágrafos e células e remove qualquer marcador que possa ter escapado
     * Executa múltiplas passadas para garantir remoção completa
     * MÉTODO CRÍTICO: Deve remover TODOS os marcadores antes de salvar o documento
     */
    private void removeTableMarkersFromDocument(XWPFDocument document) {
        try {
            // Executar múltiplas passadas para garantir remoção completa
            int maxPasses = 10; // Aumentar número de passadas
            boolean changed = true;
            
            for (int pass = 0; pass < maxPasses && changed; pass++) {
                changed = false;
                
                // Remover marcadores de todos os parágrafos
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        // Obter texto do run (tentar todos os segmentos possíveis)
                        StringBuilder fullText = new StringBuilder();
                        int segmentIndex = 0;
                        String segmentText = run.getText(segmentIndex);
                        while (segmentText != null) {
                            fullText.append(segmentText);
                            segmentIndex++;
                            segmentText = run.getText(segmentIndex);
                        }
                        
                        if (fullText.length() > 0) {
                            String text = fullText.toString();
                            String cleanedText = removeTableMarkers(text);
                            if (!cleanedText.equals(text)) {
                                changed = true;
                                // Limpar todos os segmentos de texto e adicionar o texto limpo
                                for (int i = segmentIndex - 1; i >= 0; i--) {
                                    run.setText("", i);
                                }
                                if (!cleanedText.isEmpty()) {
                                    run.setText(cleanedText, 0);
                                }
                            }
                        }
                    }
                }
                
                // Remover marcadores de todas as células das tabelas
                for (org.apache.poi.xwpf.usermodel.XWPFTable table : document.getTables()) {
                    for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                        for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                                for (XWPFRun run : paragraph.getRuns()) {
                                    // Obter texto do run (tentar todos os segmentos possíveis)
                                    StringBuilder fullText = new StringBuilder();
                                    int segmentIndex = 0;
                                    String segmentText = run.getText(segmentIndex);
                                    while (segmentText != null) {
                                        fullText.append(segmentText);
                                        segmentIndex++;
                                        segmentText = run.getText(segmentIndex);
                                    }
                                    
                                    if (fullText.length() > 0) {
                                        String text = fullText.toString();
                                        String cleanedText = removeTableMarkers(text);
                                        if (!cleanedText.equals(text)) {
                                            changed = true;
                                            // Limpar todos os segmentos de texto e adicionar o texto limpo
                                            for (int i = segmentIndex - 1; i >= 0; i--) {
                                                run.setText("", i);
                                            }
                                            if (!cleanedText.isEmpty()) {
                                                run.setText(cleanedText, 0);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // VERIFICAÇÃO FINAL ABSOLUTA: Executar uma última passada ultra-agressiva
            // Usar o método melhorado removeTableMarkers() que aplica múltiplos métodos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    StringBuilder fullText = new StringBuilder();
                    int segmentIndex = 0;
                    String segmentText = run.getText(segmentIndex);
                    while (segmentText != null) {
                        fullText.append(segmentText);
                        segmentIndex++;
                        segmentText = run.getText(segmentIndex);
                    }
                    
                    if (fullText.length() > 0) {
                        String text = fullText.toString();
                        // Usar método melhorado que aplica múltiplos métodos de remoção
                        String cleanedText = removeTableMarkers(text);
                        if (!cleanedText.equals(text)) {
                            // Limpar todos os segmentos e adicionar texto limpo
                            for (int i = segmentIndex - 1; i >= 0; i--) {
                                run.setText("", i);
                            }
                            if (!cleanedText.isEmpty()) {
                                run.setText(cleanedText, 0);
                            }
                        }
                    }
                }
            }
            
            // Verificação final nas células das tabelas
            for (org.apache.poi.xwpf.usermodel.XWPFTable table : document.getTables()) {
                for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                StringBuilder fullText = new StringBuilder();
                                int segmentIndex = 0;
                                String segmentText = run.getText(segmentIndex);
                                while (segmentText != null) {
                                    fullText.append(segmentText);
                                    segmentIndex++;
                                    segmentText = run.getText(segmentIndex);
                                }
                                
                                if (fullText.length() > 0) {
                                    String text = fullText.toString();
                                    // Usar método melhorado que aplica múltiplos métodos de remoção
                                    String cleanedText = removeTableMarkers(text);
                                    if (!cleanedText.equals(text)) {
                                        // Limpar todos os segmentos e adicionar texto limpo
                                        for (int i = segmentIndex - 1; i >= 0; i--) {
                                            run.setText("", i);
                                        }
                                        if (!cleanedText.isEmpty()) {
                                            run.setText(cleanedText, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao remover marcadores de tabela do documento: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Aplica conteúdo a uma célula, tratando quebras de linha e múltiplos runs
     */
    private void applyCellContent(XWPFTableCell cell, List<RunContent> editedCell) {
        // Limpar todos os parágrafos existentes
        while (cell.getParagraphs().size() > 0) {
            cell.removeParagraph(0);
        }
        
        if (editedCell == null || editedCell.isEmpty()) {
            // Célula vazia - criar um parágrafo vazio
            XWPFParagraph emptyPara = cell.addParagraph();
            emptyPara.createRun().setText("");
            return;
        }
        
        // Processar runs e criar parágrafos conforme necessário
        XWPFParagraph currentPara = null;
        boolean hasContent = false;
        
        for (RunContent runContent : editedCell) {
            String text = runContent.getText();
            if (text == null) {
                text = "";
            }
            
            // REMOVER MARCADORES DE TABELA (verificação de segurança)
            // Múltiplas passadas para garantir remoção completa
            text = removeTableMarkers(text);
            text = removeTableMarkers(text); // Segunda passada
            // Verificação final: se ainda contém marcadores, usar método agressivo
            if (text.contains("[TABLE_START]") || text.contains("[TABLE_END]") || 
                text.contains("[ROW_END]") || text.contains("[CELL_SEP]")) {
                String[] parts = text.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                text = String.join("", parts);
            }
            
            // Tratar texto "Não consta" ou variações
            if (text.trim().equalsIgnoreCase("Não consta") || 
                text.trim().equalsIgnoreCase("Nao consta") ||
                text.trim().equalsIgnoreCase("não consta")) {
                text = "Não consta";
            }
            
            // Se o texto contém quebras de linha, criar novos parágrafos
            if (text.contains("\n")) {
                String[] lines = text.split("\n", -1);
                for (int i = 0; i < lines.length; i++) {
                    // Criar novo parágrafo se necessário
                    if (currentPara == null || i > 0) {
                        currentPara = cell.addParagraph();
                    }
                    
                    // Adicionar conteúdo da linha
                    if (!lines[i].isEmpty() || i < lines.length - 1) {
                        XWPFRun run = currentPara.createRun();
                        // REMOVER MARCADORES DE TABELA (verificação adicional para linhas divididas)
                        String lineText = removeTableMarkers(lines[i]);
                        lineText = removeTableMarkers(lineText); // Segunda passada
                        // Verificação final: se ainda contém marcadores, usar método agressivo
                        if (lineText.contains("[TABLE_START]") || lineText.contains("[TABLE_END]") || 
                            lineText.contains("[ROW_END]") || lineText.contains("[CELL_SEP]")) {
                            String[] parts = lineText.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                            lineText = String.join("", parts);
                        }
                        run.setText(lineText);
                        if (runContent.isBold()) run.setBold(true);
                        if (runContent.isItalic()) run.setItalic(true);
                        if (runContent.isUnderline()) {
                            run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                        }
                        hasContent = true;
                    }
                }
            } else {
                // Texto sem quebras de linha
                if (currentPara == null) {
                    currentPara = cell.addParagraph();
                }
                
                // Adicionar run sempre, mesmo se vazio, para manter estrutura
                XWPFRun run = currentPara.createRun();
                run.setText(text);
                if (runContent.isBold()) run.setBold(true);
                if (runContent.isItalic()) run.setItalic(true);
                if (runContent.isUnderline()) {
                    run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                }
                if (!text.trim().isEmpty()) {
                    hasContent = true;
                }
            }
        }
        
        // Se não há conteúdo, garantir que há pelo menos um parágrafo vazio
        if (!hasContent && cell.getParagraphs().isEmpty()) {
            cell.addParagraph().createRun().setText("");
        }
    }
    
    /**
     * Converte um documento DOCX (byte array) para PDF usando Docx4J
     * Baseado EXATAMENTE no artigo: https://www.javathinking.com/blog/converting-docx-into-pdf-in-java/
     * Segue o passo a passo do artigo para preservar imagens, formatação, tabelas e outros elementos
     * 
     * Nota: Na versão 11.4.9 do docx4j, PdfSettings não está disponível diretamente.
     * Usamos FOSettings com configurações adequadas para preservar tabelas e formatação.
     * O artigo menciona embedFonts=true - isso é feito via FOP Factory na versão 11.4.9.
     */
    private byte[] convertDocxToPdfWithDocx4J(byte[] docxBytes) throws Exception {
        try (ByteArrayInputStream docxInputStream = new ByteArrayInputStream(docxBytes);
             ByteArrayOutputStream foOutputStream = new ByteArrayOutputStream();
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            
            // Step 1: Load the DOCX file (conforme artigo)
            WordprocessingMLPackage wordMLPackage = Docx4J.load(docxInputStream);
            
            // Step 2: Configure FOSettings para preservar formatação e tabelas (conforme artigo)
            // O artigo menciona embedFonts=true - isso é feito via FOSettings na versão 11.4.9
            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(wordMLPackage);
            
            // Configurar FOP Factory para PDF com suporte a fontes embutidas
            FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new java.io.File(".").toURI());
            FopFactory fopFactory = fopFactoryBuilder.build();
            
            // Configurar FOUserAgent para preservar formatação de tabelas
            FORendererApacheFOP.getFOUserAgent(foSettings, fopFactory);
            
            // Step 3: Converter DOCX para XSL-FO (preserva tabelas, imagens, formatação)
            // Conforme artigo: preservar bordas de tabelas, células mescladas, imagens embutidas
            Docx4J.toFO(foSettings, foOutputStream, Docx4J.FLAG_NONE);
            
            // Validar XSL-FO gerado
            byte[] foBytes = foOutputStream.toByteArray();
            if (foBytes.length == 0) {
                throw new IOException("XSL-FO gerado está vazio. Verifique se o DOCX foi criado corretamente.");
            }
            
            // Verificar se o XSL-FO começa com XML válido (remover BOM se existir)
            String foContent = new String(foBytes);
            if (foContent.startsWith("\uFEFF")) {
                foContent = foContent.substring(1);
                foBytes = foContent.getBytes();
            }
            foContent = foContent.trim();
            if (!foContent.startsWith("<?xml")) {
                int xmlStart = foContent.indexOf("<?xml");
                if (xmlStart > 0) {
                    foContent = foContent.substring(xmlStart);
                    foBytes = foContent.getBytes();
                } else {
                    throw new IOException("XSL-FO gerado não contém XML válido.");
                }
            }
            
            // Converter XSL-FO para PDF usando Apache FOP (conforme artigo - preserva tabelas)
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOutputStream);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            
            Source src = new StreamSource(new ByteArrayInputStream(foBytes));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
            
            byte[] pdfBytes = pdfOutputStream.toByteArray();
            log.info("PDF gerado com sucesso usando Docx4J (conforme artigo) - Tamanho: {} bytes", pdfBytes.length);
            return pdfBytes;
        } catch (Exception e) {
            log.error("Erro ao converter DOCX para PDF usando Docx4J (conforme artigo): {}", e.getMessage(), e);
            throw new IOException("Erro ao converter documento para PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converte um documento DOCX (byte array) para PDF
     * Usa docx4j com Apache FOP para preservar imagens, formatação, tabelas e outros elementos
     * Método legado mantido para compatibilidade
     */
    @SuppressWarnings("deprecation")
    private byte[] convertDocxToPdf(byte[] docxBytes) throws Exception {
        try (ByteArrayInputStream docxInputStream = new ByteArrayInputStream(docxBytes);
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
             ByteArrayOutputStream foOutputStream = new ByteArrayOutputStream()) {
            
            // Carregar documento DOCX usando docx4j
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);
            
            // Configurar FOP Factory
            FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new java.io.File(".").toURI());
            FopFactory fopFactory = fopFactoryBuilder.build();
            
            // Configurar FOSettings para converter DOCX para XSL-FO
            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(wordMLPackage);
            
            // IMPORTANTE: Configurar o FOUserAgent no FORendererApacheFOP ANTES de usar
            // Isso deve ser feito através do método estático getFOUserAgent() com os parâmetros corretos
            FORendererApacheFOP.getFOUserAgent(foSettings, fopFactory);
            
            // Converter DOCX para XSL-FO
            Docx4J.toFO(foSettings, foOutputStream, Docx4J.FLAG_NONE);
            
            // Validar XSL-FO gerado antes de converter para PDF
            byte[] foBytes = foOutputStream.toByteArray();
            if (foBytes.length == 0) {
                throw new IOException("XSL-FO gerado está vazio. Verifique se o DOCX foi criado corretamente.");
            }
            
            // Verificar se o XSL-FO começa com XML válido (remover BOM se existir)
            String foContent = new String(foBytes);
            // Remover BOM (Byte Order Mark) se existir
            if (foContent.startsWith("\uFEFF")) {
                foContent = foContent.substring(1);
                foBytes = foContent.getBytes();
            }
            // Remover caracteres não-printáveis antes do XML prolog
            foContent = foContent.trim();
            if (!foContent.startsWith("<?xml")) {
                // Tentar encontrar o início do XML
                int xmlStart = foContent.indexOf("<?xml");
                if (xmlStart > 0) {
                    foContent = foContent.substring(xmlStart);
                    foBytes = foContent.getBytes();
                    log.warn("XSL-FO tinha {} caracteres antes do XML prolog, removidos", xmlStart);
                } else {
                    throw new IOException("XSL-FO gerado não contém XML válido. Primeiros 200 caracteres: " + 
                                        foContent.substring(0, Math.min(200, foContent.length())));
                }
            }
            
            log.debug("XSL-FO gerado - Tamanho: {} bytes, Início: {}", foBytes.length, 
                     foContent.substring(0, Math.min(100, foContent.length())));
            
            // Converter XSL-FO para PDF usando Apache FOP
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOutputStream);
            
            // Transformar XSL-FO para PDF
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            
            Source src = new StreamSource(new ByteArrayInputStream(foBytes));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
            
            return pdfOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao converter DOCX para PDF usando docx4j: {}", e.getMessage(), e);
            throw new IOException("Erro ao converter documento para PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Classe auxiliar para armazenar conteúdo e formatação de um run individual
     */
    private static class RunContent {
        private String text;
        private boolean bold;
        private boolean italic;
        private boolean underline;
        
        public RunContent(String text) {
            this.text = text;
        }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public boolean isBold() { return bold; }
        public void setBold(boolean bold) { this.bold = bold; }
        public boolean isItalic() { return italic; }
        public void setItalic(boolean italic) { this.italic = italic; }
        public boolean isUnderline() { return underline; }
        public void setUnderline(boolean underline) { this.underline = underline; }
    }
    
    /**
     * Classe auxiliar para armazenar conteúdo e formatação de parágrafo
     * Agora contém múltiplos runs para preservar formatação granular
     */
    private static class ParagraphContent {
        private List<RunContent> runs;
        
        public ParagraphContent() {
            this.runs = new ArrayList<>();
        }
        
        public void addRun(RunContent run) {
            this.runs.add(run);
        }
        
        public List<RunContent> getRuns() { return runs; }
        
        // Métodos de compatibilidade para código existente
        public String getText() {
            return runs.stream()
                    .map(RunContent::getText)
                    .reduce("", (a, b) -> a + b);
        }
        
        public boolean isBold() {
            return runs.stream().anyMatch(RunContent::isBold);
        }
        
        public boolean isItalic() {
            return runs.stream().anyMatch(RunContent::isItalic);
        }
        
        public boolean isUnderline() {
            return runs.stream().anyMatch(RunContent::isUnderline);
        }
    }
    
    /**
     * Estrutura para representar conteúdo do documento (parágrafos e tabelas)
     */
    private static class DocumentContent {
        List<ParagraphContent> paragraphs = new ArrayList<>();
        List<TableContent> tables = new ArrayList<>();
        
        public List<ParagraphContent> getParagraphs() { return paragraphs; }
        public List<TableContent> getTables() { return tables; }
    }
    
    /**
     * Estrutura para representar conteúdo de uma tabela
     */
    private static class TableContent {
        List<List<List<RunContent>>> rows = new ArrayList<>(); // Lista de linhas, cada linha tem células, cada célula tem runs
        
        public void addRow(List<List<RunContent>> row) {
            rows.add(row);
        }
        
        public List<List<List<RunContent>>> getRows() {
            return rows;
        }
    }
    
    /**
     * Processa texto e extrai marcadores de tabela, retornando lista de partes (texto e marcadores)
     */
    private static class TextPart {
        String text;
        boolean isMarker;
        
        TextPart(String text, boolean isMarker) {
            this.text = text;
            this.isMarker = isMarker;
        }
    }
    
    /**
     * Extrai marcadores de tabela de um texto, retornando lista de partes
     */
    private List<TextPart> extractTableMarkers(String text) {
        List<TextPart> parts = new ArrayList<>();
        String[] markers = {"[TABLE_START]", "[TABLE_END]", "[ROW_END]", "[CELL_SEP]"};
        
        int lastIndex = 0;
        while (lastIndex < text.length()) {
            int nextMarkerIndex = -1;
            String nextMarker = null;
            
            // Encontrar o próximo marcador
            for (String marker : markers) {
                int index = text.indexOf(marker, lastIndex);
                if (index != -1 && (nextMarkerIndex == -1 || index < nextMarkerIndex)) {
                    nextMarkerIndex = index;
                    nextMarker = marker;
                }
            }
            
            if (nextMarkerIndex == -1) {
                // Não há mais marcadores, adicionar texto restante
                if (lastIndex < text.length()) {
                    String remaining = text.substring(lastIndex);
                    if (!remaining.isEmpty()) {
                        parts.add(new TextPart(remaining, false));
                    }
                }
                break;
            }
            
            // Adicionar texto antes do marcador
            if (nextMarkerIndex > lastIndex) {
                String beforeMarker = text.substring(lastIndex, nextMarkerIndex);
                if (!beforeMarker.isEmpty()) {
                    parts.add(new TextPart(beforeMarker, false));
                }
            }
            
            // Adicionar marcador
            parts.add(new TextPart(nextMarker, true));
            
            lastIndex = nextMarkerIndex + nextMarker.length();
        }
        
        return parts;
    }
    
    /**
     * Converte Quill Delta operations para estrutura de documento (parágrafos e tabelas)
     * Processa marcadores de tabela e cria estrutura separada para tabelas
     */
    private DocumentContent convertDeltaToDocumentContent(JsonNode ops) {
        DocumentContent docContent = new DocumentContent();
        ParagraphContent currentParagraph = new ParagraphContent();
        StringBuilder currentRunText = new StringBuilder();
        boolean currentBold = false;
        boolean currentItalic = false;
        boolean currentUnderline = false;
        
        // Estado para processamento de tabelas
        TableContent currentTable = null;
        List<List<RunContent>> currentRow = null;
        List<RunContent> currentCell = null;
        StringBuilder currentCellText = new StringBuilder();
        boolean inTable = false;
        boolean inCell = false;
        
        for (JsonNode op : ops) {
            if (op.has("insert")) {
                JsonNode insert = op.get("insert");
                if (insert.isTextual()) {
                    String text = insert.asText();
                    
                    // Processar texto e extrair marcadores
                    List<TextPart> parts = extractTableMarkers(text);
                    
                    // Verificar atributos de formatação uma vez por operação
                    boolean opBold = false;
                    boolean opItalic = false;
                    boolean opUnderline = false;
                    
                    if (op.has("attributes")) {
                        JsonNode attrs = op.get("attributes");
                        opBold = attrs.has("bold") && attrs.get("bold").asBoolean();
                        opItalic = attrs.has("italic") && attrs.get("italic").asBoolean();
                        opUnderline = attrs.has("underline") && attrs.get("underline").asBoolean();
                    }
                    
                    for (TextPart part : parts) {
                        if (part.isMarker) {
                            // Processar marcador de tabela
                            if (part.text.equals("[TABLE_START]")) {
                                // Finalizar parágrafo atual se houver
                                if (currentRunText.length() > 0) {
                                    RunContent run = new RunContent(currentRunText.toString());
                                    run.setBold(currentBold);
                                    run.setItalic(currentItalic);
                                    run.setUnderline(currentUnderline);
                                    currentParagraph.addRun(run);
                                    currentRunText = new StringBuilder();
                                }
                                if (!currentParagraph.getRuns().isEmpty()) {
                                    docContent.getParagraphs().add(currentParagraph);
                                    currentParagraph = new ParagraphContent();
                                }
                                
                                // Iniciar nova tabela
                                currentTable = new TableContent();
                                inTable = true;
                                inCell = false;
                                continue;
                            } else if (part.text.equals("[TABLE_END]")) {
                                // Finalizar célula atual se houver texto
                                if (inCell) {
                                    if (currentCell == null) {
                                        currentCell = new ArrayList<>();
                                    }
                                    if (currentCellText.length() > 0) {
                                        RunContent run = new RunContent(currentCellText.toString());
                                        run.setBold(currentBold);
                                        run.setItalic(currentItalic);
                                        run.setUnderline(currentUnderline);
                                        currentCell.add(run);
                                        currentCellText = new StringBuilder();
                                    }
                                }
                                
                                // Finalizar linha atual - sempre adicionar célula, mesmo se vazia
                                if (currentCell != null) {
                                    if (currentRow == null) {
                                        currentRow = new ArrayList<>();
                                    }
                                    currentRow.add(currentCell);
                                    currentCell = null;
                                }
                                
                                // Finalizar tabela
                                if (currentRow != null && !currentRow.isEmpty()) {
                                    currentTable.addRow(currentRow);
                                    currentRow = null;
                                }
                                
                                if (currentTable != null && !currentTable.getRows().isEmpty()) {
                                    docContent.getTables().add(currentTable);
                                }
                                currentTable = null;
                                inTable = false;
                                inCell = false;
                                continue;
                            } else if (part.text.equals("[ROW_END]")) {
                                // Finalizar célula atual se houver texto
                                if (inCell) {
                                    if (currentCell == null) {
                                        currentCell = new ArrayList<>();
                                    }
                                    if (currentCellText.length() > 0) {
                                        RunContent run = new RunContent(currentCellText.toString());
                                        run.setBold(currentBold);
                                        run.setItalic(currentItalic);
                                        run.setUnderline(currentUnderline);
                                        currentCell.add(run);
                                        currentCellText = new StringBuilder();
                                    }
                                }
                                
                                // Finalizar linha atual - sempre adicionar célula, mesmo se vazia
                                if (currentCell != null) {
                                    if (currentRow == null) {
                                        currentRow = new ArrayList<>();
                                    }
                                    currentRow.add(currentCell);
                                    currentCell = new ArrayList<>();
                                }
                                
                                if (currentRow != null && !currentRow.isEmpty()) {
                                    currentTable.addRow(currentRow);
                                    currentRow = new ArrayList<>();
                                }
                                continue;
                            } else if (part.text.equals("[CELL_SEP]")) {
                                // Garantir que estamos em uma célula
                                if (!inCell) {
                                    inCell = true;
                                }
                                
                                // Finalizar célula atual se houver texto
                                if (currentCell == null) {
                                    currentCell = new ArrayList<>();
                                }
                                // Adicionar run mesmo se vazio para preservar células vazias
                                if (currentCellText.length() > 0) {
                                    RunContent run = new RunContent(currentCellText.toString());
                                    run.setBold(currentBold);
                                    run.setItalic(currentItalic);
                                    run.setUnderline(currentUnderline);
                                    currentCell.add(run);
                                } else {
                                    // Célula vazia - adicionar run vazio para manter estrutura
                                    RunContent emptyRun = new RunContent("");
                                    emptyRun.setBold(currentBold);
                                    emptyRun.setItalic(currentItalic);
                                    emptyRun.setUnderline(currentUnderline);
                                    currentCell.add(emptyRun);
                                }
                                currentCellText = new StringBuilder();
                                
                                // Sempre adicionar célula à linha, mesmo se vazia
                                if (currentRow == null) {
                                    currentRow = new ArrayList<>();
                                }
                                currentRow.add(currentCell);
                                currentCell = new ArrayList<>();
                                continue;
                            }
                        } else {
                            // Processar texto normal (não é marcador)
                            // CRÍTICO: Remover TODOS os marcadores antes de processar
                            // Executar múltiplas passadas para garantir remoção completa
                            String cleanedText = part.text;
                            int maxCleaningPasses = 5;
                            for (int pass = 0; pass < maxCleaningPasses; pass++) {
                                String previousText = cleanedText;
                                cleanedText = removeTableMarkers(cleanedText);
                                // Se não houve mudança, parar
                                if (cleanedText.equals(previousText)) {
                                    break;
                                }
                            }
                            
                            // Verificação final: se ainda contém marcadores, usar método mais agressivo
                            if (cleanedText.contains("[TABLE_START]") || cleanedText.contains("[TABLE_END]") || 
                                cleanedText.contains("[ROW_END]") || cleanedText.contains("[CELL_SEP]")) {
                                // Dividir por marcadores e juntar apenas partes válidas
                                String[] textParts = cleanedText.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                                cleanedText = String.join("", textParts);
                            }
                            
                            String escapedText = DocumentSanitizer.escapeForWord(cleanedText);
                            
                            if (inTable) {
                                // Processar conteúdo de tabela
                                inCell = true;
                                if (currentCell == null) {
                                    currentCell = new ArrayList<>();
                                }
                                if (currentRow == null) {
                                    currentRow = new ArrayList<>();
                                }
                                
                                // Se a formatação mudou, finalizar run atual
                                if (currentCellText.length() > 0 && 
                                    (opBold != currentBold || opItalic != currentItalic || opUnderline != currentUnderline)) {
                                    RunContent run = new RunContent(currentCellText.toString());
                                    run.setBold(currentBold);
                                    run.setItalic(currentItalic);
                                    run.setUnderline(currentUnderline);
                                    currentCell.add(run);
                                    currentCellText = new StringBuilder();
                                }
                                
                                currentBold = opBold;
                                currentItalic = opItalic;
                                currentUnderline = opUnderline;
                                
                                // Processar quebras de linha dentro de células
                                if (escapedText.contains("\n")) {
                                    String[] lines = escapedText.split("\n", -1);
                                    for (int i = 0; i < lines.length; i++) {
                                        // REMOVER MARCADORES ANTES DE ADICIONAR AO STRINGBUILDER
                                        String cleanLine = removeTableMarkers(lines[i]);
                                        if (cleanLine.length() > 0) {
                                            currentCellText.append(cleanLine);
                                        }
                                        if (i < lines.length - 1) {
                                            // Finalizar run atual se houver conteúdo
                                            if (currentCellText.length() > 0) {
                                                // REMOVER MARCADORES ANTES DE CRIAR RUN
                                                String finalText = removeTableMarkers(currentCellText.toString());
                                                RunContent run = new RunContent(finalText);
                                                run.setBold(currentBold);
                                                run.setItalic(currentItalic);
                                                run.setUnderline(currentUnderline);
                                                currentCell.add(run);
                                                currentCellText = new StringBuilder();
                                            }
                                        }
                                    }
                                } else {
                                    // REMOVER MARCADORES ANTES DE ADICIONAR AO STRINGBUILDER
                                    String cleanEscapedText = removeTableMarkers(escapedText);
                                    currentCellText.append(cleanEscapedText);
                                }
                            } else {
                                // Processar conteúdo de parágrafo normal
                                // Se a formatação mudou, finalizar o run atual e começar um novo
                                if (currentRunText.length() > 0 && 
                                    (opBold != currentBold || opItalic != currentItalic || opUnderline != currentUnderline)) {
                                    RunContent run = new RunContent(currentRunText.toString());
                                    run.setBold(currentBold);
                                    run.setItalic(currentItalic);
                                    run.setUnderline(currentUnderline);
                                    currentParagraph.addRun(run);
                                    currentRunText = new StringBuilder();
                                }
                                
                                // Atualizar formatação atual
                                currentBold = opBold;
                                currentItalic = opItalic;
                                currentUnderline = opUnderline;
                                
                                // Processar quebras de linha
                                if (escapedText.contains("\n")) {
                                    String[] lines = escapedText.split("\n", -1);
                                    for (int i = 0; i < lines.length; i++) {
                                        // REMOVER MARCADORES ANTES DE ADICIONAR AO STRINGBUILDER
                                        String cleanLine = removeTableMarkers(lines[i]);
                                        currentRunText.append(cleanLine);
                                        
                                        if (i < lines.length - 1) {
                                            // Finalizar run atual se houver texto
                                            if (currentRunText.length() > 0) {
                                                // REMOVER MARCADORES ANTES DE CRIAR RUN
                                                String finalText = removeTableMarkers(currentRunText.toString());
                                                RunContent run = new RunContent(finalText);
                                                run.setBold(currentBold);
                                                run.setItalic(currentItalic);
                                                run.setUnderline(currentUnderline);
                                                currentParagraph.addRun(run);
                                                currentRunText = new StringBuilder();
                                            }
                                            
                                            // Finalizar parágrafo atual e começar novo
                                            if (!currentParagraph.getRuns().isEmpty()) {
                                                docContent.getParagraphs().add(currentParagraph);
                                            }
                                            currentParagraph = new ParagraphContent();
                                        }
                                    }
                                } else {
                                    // REMOVER MARCADORES ANTES DE ADICIONAR AO STRINGBUILDER
                                    String cleanEscapedText = removeTableMarkers(escapedText);
                                    currentRunText.append(cleanEscapedText);
                                }
                            }
                        }
                    }
                }
            } else if (op.has("delete")) {
                // Remover caracteres - simplificado, não trata tabelas
                if (!inTable) {
                    int deleteCount = op.get("delete").asInt();
                    if (currentRunText.length() >= deleteCount) {
                        currentRunText.setLength(currentRunText.length() - deleteCount);
                    } else {
                        int remaining = deleteCount - currentRunText.length();
                        currentRunText.setLength(0);
                        while (remaining > 0 && !currentParagraph.getRuns().isEmpty()) {
                            RunContent lastRun = currentParagraph.getRuns().get(currentParagraph.getRuns().size() - 1);
                            if (lastRun.getText().length() <= remaining) {
                                remaining -= lastRun.getText().length();
                                currentParagraph.getRuns().remove(currentParagraph.getRuns().size() - 1);
                            } else {
                                String newText = lastRun.getText().substring(0, lastRun.getText().length() - remaining);
                                lastRun = new RunContent(newText);
                                lastRun.setBold(currentBold);
                                lastRun.setItalic(currentItalic);
                                lastRun.setUnderline(currentUnderline);
                                currentParagraph.getRuns().set(currentParagraph.getRuns().size() - 1, lastRun);
                                remaining = 0;
                            }
                        }
                    }
                }
            }
        }
        
        // Finalizar conteúdo pendente
        if (inTable && inCell) {
            if (currentCell == null) {
                currentCell = new ArrayList<>();
            }
            if (currentCellText.length() > 0) {
                // REMOVER MARCADORES ANTES DE CRIAR RUN
                String cleanedCellText = removeTableMarkers(currentCellText.toString());
                if (!cleanedCellText.isEmpty()) {
                    RunContent run = new RunContent(cleanedCellText);
                    run.setBold(currentBold);
                    run.setItalic(currentItalic);
                    run.setUnderline(currentUnderline);
                    currentCell.add(run);
                }
            } else if (currentCell.isEmpty()) {
                // Célula vazia - adicionar run vazio para manter estrutura
                RunContent emptyRun = new RunContent("");
                emptyRun.setBold(currentBold);
                emptyRun.setItalic(currentItalic);
                emptyRun.setUnderline(currentUnderline);
                currentCell.add(emptyRun);
            }
        }
        
        if (inTable && currentCell != null && !currentCell.isEmpty()) {
            if (currentRow == null) {
                currentRow = new ArrayList<>();
            }
            currentRow.add(currentCell);
        }
        
        if (inTable && currentRow != null && !currentRow.isEmpty()) {
            if (currentTable == null) {
                currentTable = new TableContent();
            }
            currentTable.addRow(currentRow);
        }
        
        if (inTable && currentTable != null && !currentTable.getRows().isEmpty()) {
            docContent.getTables().add(currentTable);
        }
        
        // Adicionar último run se houver texto
        if (currentRunText.length() > 0) {
            String finalText = removeTableMarkers(currentRunText.toString());
            if (!finalText.isEmpty()) {
                RunContent run = new RunContent(finalText);
                run.setBold(currentBold);
                run.setItalic(currentItalic);
                run.setUnderline(currentUnderline);
                currentParagraph.addRun(run);
            }
        }
        
        // Adicionar último parágrafo se não estiver vazio
        if (!currentParagraph.getRuns().isEmpty()) {
            docContent.getParagraphs().add(currentParagraph);
        }
        
        // SANITIZAÇÃO FINAL: Remover marcadores de todos os runs em parágrafos e células
        // Executar múltiplas passadas para garantir remoção completa (aumentado para 5 passadas)
        for (int pass = 0; pass < 5; pass++) {
            for (ParagraphContent para : docContent.getParagraphs()) {
                for (RunContent run : para.getRuns()) {
                    String text = run.getText();
                    if (text != null) {
                        // Usar método melhorado que aplica múltiplos métodos de remoção
                        String cleanedText = removeTableMarkers(text);
                        // Verificação adicional: se ainda contém, aplicar método agressivo
                        if (cleanedText.contains("[TABLE_START]") || cleanedText.contains("[TABLE_END]") || 
                            cleanedText.contains("[ROW_END]") || cleanedText.contains("[CELL_SEP]")) {
                            String[] parts = cleanedText.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                            cleanedText = String.join("", parts);
                        }
                        run.setText(cleanedText);
                    }
                }
            }
            
            for (TableContent table : docContent.getTables()) {
                for (List<List<RunContent>> row : table.getRows()) {
                    for (List<RunContent> cell : row) {
                        for (RunContent run : cell) {
                            String text = run.getText();
                            if (text != null) {
                                // Usar método melhorado que aplica múltiplos métodos de remoção
                                String cleanedText = removeTableMarkers(text);
                                // Verificação adicional: se ainda contém, aplicar método agressivo
                                if (cleanedText.contains("[TABLE_START]") || cleanedText.contains("[TABLE_END]") || 
                                    cleanedText.contains("[ROW_END]") || cleanedText.contains("[CELL_SEP]")) {
                                    String[] parts = cleanedText.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                                    cleanedText = String.join("", parts);
                                }
                                run.setText(cleanedText);
                            }
                        }
                    }
                }
            }
        }
        
        return docContent;
    }
    
    /**
     * Converte Quill Delta operations para lista de parágrafos com formatação granular
     * Cria múltiplos runs quando a formatação muda dentro de um parágrafo
     * @deprecated Use convertDeltaToDocumentContent instead
     */
    @Deprecated
    private List<ParagraphContent> convertDeltaToParagraphsWithFormatting(JsonNode ops) {
        DocumentContent docContent = convertDeltaToDocumentContent(ops);
        return docContent.getParagraphs();
    }
    
    /**
     * Extrai conteúdo do documento Word com dados do processo substituídos
     * Retorna estrutura com texto e identificação de dados do processo
     */
    public DocumentContentResponseDTO getDocumentContentForProcess(Long processId, String templateName) throws IOException {
        // Validar templateName
        if (!DocumentSanitizer.validateTemplateName(templateName)) {
            throw new IllegalArgumentException("Nome de template inválido: " + templateName);
        }
        
        // Buscar processo com todos os relacionamentos necessários
        Process process = processRepository.findByIdWithRelations(processId)
                .orElseThrow(() -> new ProcessNotFoundException(processId));
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new DocumentTemplateNotFoundException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar mapa de dados para substituição (sanitizado)
        Map<String, String> dataMap = buildDataMap(process);
        Map<String, String> sanitizedDataMap = new HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            sanitizedDataMap.put(entry.getKey(), DocumentSanitizer.sanitizeText(entry.getValue()));
        }
        
        // Criar mapa de valores originais (não sanitizados) para identificar dados do processo
        Map<String, String> originalDataMap = buildDataMap(process);
        Set<String> processDataValues = new HashSet<>(originalDataMap.values());
        processDataValues.remove(""); // Remover valores vazios
        
        // Extrair conteúdo do documento
        List<DocumentContentResponseDTO.ContentBlock> contentBlocks = new ArrayList<>();
        
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            
            // Processar parágrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                List<RunContent> runs = extractRunsFromParagraph(paragraph, sanitizedDataMap);
                if (!runs.isEmpty()) {
                    // Combinar texto de todos os runs para verificar se contém dados do processo
                    String paragraphText = runs.stream()
                            .map(RunContent::getText)
                            .reduce("", (a, b) -> a + b);
                    
                    if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                        // Identificar se contém dados do processo
                        boolean isProcessData = processDataValues.stream()
                                .anyMatch(value -> paragraphText.contains(value));
                        
                        // Criar um bloco para cada run para preservar formatação granular
                        for (RunContent run : runs) {
                            DocumentContentResponseDTO.ContentBlock block = new DocumentContentResponseDTO.ContentBlock();
                            block.setText(run.getText());
                            block.setClientData(isProcessData); // Reutilizando o campo para dados do processo
                            
                            // Adicionar formatação ao bloco
                            Map<String, Object> formatting = new HashMap<>();
                            if (run.isBold()) formatting.put("bold", true);
                            if (run.isItalic()) formatting.put("italic", true);
                            if (run.isUnderline()) formatting.put("underline", true);
                            block.setFormatting(formatting);
                            
                            contentBlocks.add(block);
                        }
                        
                        // Adicionar quebra de linha após o parágrafo
                        DocumentContentResponseDTO.ContentBlock lineBreak = 
                            new DocumentContentResponseDTO.ContentBlock();
                        lineBreak.setText("\n");
                        lineBreak.setClientData(false);
                        lineBreak.setFormatting(new HashMap<>());
                        contentBlocks.add(lineBreak);
                    }
                }
            }
            
            // Processar tabelas - marcar como tabela para preservar estrutura
            document.getTables().forEach(table -> {
                // Adicionar marcador de início de tabela
                DocumentContentResponseDTO.ContentBlock tableStart = new DocumentContentResponseDTO.ContentBlock();
                tableStart.setText("[TABLE_START]");
                tableStart.setClientData(false);
                Map<String, Object> tableFormatting = new HashMap<>();
                tableFormatting.put("isTable", true);
                tableStart.setFormatting(tableFormatting);
                contentBlocks.add(tableStart);
                
                table.getRows().forEach(row -> {
                    int cellCount = 0;
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        List<RunContent> cellRuns = new ArrayList<>();
                        cell.getParagraphs().forEach(paragraph -> {
                            cellRuns.addAll(extractRunsFromParagraph(paragraph, sanitizedDataMap));
                        });
                        
                        if (!cellRuns.isEmpty()) {
                            String cellText = cellRuns.stream()
                                    .map(RunContent::getText)
                                    .reduce("", (a, b) -> a + b);
                            
                            if (cellText != null && !cellText.trim().isEmpty()) {
                                boolean isProcessData = processDataValues.stream()
                                        .anyMatch(value -> cellText.contains(value));
                                
                                // Criar bloco para cada run da célula
                                for (RunContent run : cellRuns) {
                                    DocumentContentResponseDTO.ContentBlock block = 
                                        new DocumentContentResponseDTO.ContentBlock();
                                    block.setText(run.getText());
                                    block.setClientData(isProcessData);
                                    
                                    Map<String, Object> formatting = new HashMap<>();
                                    formatting.put("isTableCell", true);
                                    if (run.isBold()) formatting.put("bold", true);
                                    if (run.isItalic()) formatting.put("italic", true);
                                    if (run.isUnderline()) formatting.put("underline", true);
                                    block.setFormatting(formatting);
                                    
                                    contentBlocks.add(block);
                                }
                            }
                        }
                        
                        cellCount++;
                        // Adicionar separador de célula (exceto após a última célula da linha)
                        if (cellCount < row.getTableCells().size()) {
                            DocumentContentResponseDTO.ContentBlock cellSeparator = 
                                new DocumentContentResponseDTO.ContentBlock();
                            cellSeparator.setText("[CELL_SEP]");
                            cellSeparator.setClientData(false);
                            Map<String, Object> sepFormatting = new HashMap<>();
                            sepFormatting.put("isTableCell", true);
                            cellSeparator.setFormatting(sepFormatting);
                            contentBlocks.add(cellSeparator);
                        }
                    }
                    
                    // Adicionar separador de linha após cada linha (exceto a última)
                    if (row != table.getRows().get(table.getRows().size() - 1)) {
                        DocumentContentResponseDTO.ContentBlock rowSeparator = 
                            new DocumentContentResponseDTO.ContentBlock();
                        rowSeparator.setText("[ROW_END]");
                        rowSeparator.setClientData(false);
                        Map<String, Object> rowFormatting = new HashMap<>();
                        rowFormatting.put("isTable", true);
                        rowSeparator.setFormatting(rowFormatting);
                        contentBlocks.add(rowSeparator);
                    }
                });
                
                // Adicionar marcador de fim de tabela
                DocumentContentResponseDTO.ContentBlock tableEnd = new DocumentContentResponseDTO.ContentBlock();
                tableEnd.setText("[TABLE_END]\n");
                tableEnd.setClientData(false);
                Map<String, Object> endFormatting = new HashMap<>();
                endFormatting.put("isTable", true);
                tableEnd.setFormatting(endFormatting);
                contentBlocks.add(tableEnd);
            });
        }
        
        // Criar mapa de dados do processo (em maiúsculas para destacar)
        Map<String, String> processDataMap = new HashMap<>();
        for (Map.Entry<String, String> entry : originalDataMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                processDataMap.put(entry.getKey(), entry.getValue().toUpperCase());
            }
        }
        
        DocumentContentResponseDTO response = new DocumentContentResponseDTO();
        response.setTemplateName(templateName);
        response.setContent(contentBlocks);
        response.setClientData(processDataMap); // Reutilizando o campo para dados do processo
        
        return response;
    }
    
    /**
     * Gera documento Word a partir de conteúdo editado (Quill Delta) para processo
     */
    public byte[] generateCustomDocumentForProcess(Long processId, String templateName, JsonNode content) throws IOException {
        // Validar e sanitizar templateName
        if (!DocumentSanitizer.validateTemplateName(templateName)) {
            throw new IllegalArgumentException("Nome de template inválido: " + templateName);
        }
        
        // Validar e sanitizar Quill Delta
        if (!DocumentSanitizer.validateQuillDelta(content)) {
            throw new IllegalArgumentException("Conteúdo Quill Delta inválido");
        }
        
        JsonNode sanitizedDelta = DocumentSanitizer.sanitizeQuillDelta(content);
        
        // Verificar se processo existe (validação básica)
        if (!processRepository.existsById(processId)) {
            throw new ProcessNotFoundException(processId);
        }
        
        // Verificar se template existe
        if (!templateService.templateExists(templateName)) {
            throw new DocumentTemplateNotFoundException("Template não encontrado: " + templateName);
        }
        
        // Obter recurso do template
        Resource templateResource = templateService.getTemplateResource(templateName);
        
        // Criar novo documento baseado no template
        try (InputStream inputStream = templateResource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // Converter Quill Delta para conteúdo Word (parágrafos e tabelas)
            JsonNode ops = sanitizedDelta.get("ops");
            DocumentContent docContent = convertDeltaToDocumentContent(ops);
            List<ParagraphContent> paragraphs = docContent.getParagraphs();
            List<TableContent> tables = docContent.getTables();
            
            // IMPORTANTE: Preservar tabelas do template original e aplicar conteúdo editado
            
            // Limpar apenas parágrafos existentes (não tabelas) e aplicar novo conteúdo
            List<XWPFParagraph> existingParagraphs = new ArrayList<>(document.getParagraphs());
            
            // Limpar todos os runs dos parágrafos existentes
            for (XWPFParagraph paragraph : existingParagraphs) {
                int runsCount = paragraph.getRuns().size();
                for (int i = runsCount - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
            }
            
            // Aplicar conteúdo aos parágrafos existentes com múltiplos runs
            int paragraphIndex = 0;
            for (XWPFParagraph paragraph : existingParagraphs) {
                if (paragraphIndex < paragraphs.size()) {
                    ParagraphContent paraContent = paragraphs.get(paragraphIndex);
                    
                    // Criar múltiplos runs para preservar formatação granular
                    for (RunContent runContent : paraContent.getRuns()) {
                    XWPFRun run = paragraph.createRun();
                    // REMOVER MARCADORES DE TABELA (verificação de segurança)
                    String text = runContent.getText();
                    if (text != null) {
                        // Múltiplas passadas para garantir remoção completa
                        text = removeTableMarkers(text);
                        text = removeTableMarkers(text); // Segunda passada
                        // Verificação final: se ainda contém marcadores, usar método agressivo
                        if (text.contains("[TABLE_START]") || text.contains("[TABLE_END]") || 
                            text.contains("[ROW_END]") || text.contains("[CELL_SEP]")) {
                            String[] parts = text.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                            text = String.join("", parts);
                        }
                    } else {
                        text = "";
                    }
                    run.setText(text);
                        if (runContent.isBold()) run.setBold(true);
                        if (runContent.isItalic()) run.setItalic(true);
                        if (runContent.isUnderline()) {
                            run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                        }
                    }
                    paragraphIndex++;
                }
            }
            
            // Se há mais parágrafos no Delta do que no template, criar novos
            while (paragraphIndex < paragraphs.size()) {
                XWPFParagraph newParagraph = document.createParagraph();
                ParagraphContent paraContent = paragraphs.get(paragraphIndex);
                
                // Criar múltiplos runs para preservar formatação granular
                for (RunContent runContent : paraContent.getRuns()) {
                    XWPFRun run = newParagraph.createRun();
                    // REMOVER MARCADORES DE TABELA (verificação de segurança)
                    String text = runContent.getText();
                    if (text != null) {
                        // Múltiplas passadas para garantir remoção completa
                        text = removeTableMarkers(text);
                        text = removeTableMarkers(text); // Segunda passada
                        // Verificação final: se ainda contém marcadores, usar método agressivo
                        if (text.contains("[TABLE_START]") || text.contains("[TABLE_END]") || 
                            text.contains("[ROW_END]") || text.contains("[CELL_SEP]")) {
                            String[] parts = text.split("\\[TABLE_START\\]|\\[TABLE_END\\]|\\[ROW_END\\]|\\[CELL_SEP\\]");
                            text = String.join("", parts);
                        }
                    } else {
                        text = "";
                    }
                    run.setText(text);
                    if (runContent.isBold()) run.setBold(true);
                    if (runContent.isItalic()) run.setItalic(true);
                    if (runContent.isUnderline()) {
                        run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                    }
                }
                paragraphIndex++;
            }
            
            // Aplicar conteúdo editado às tabelas do template original e criar novas se necessário
            List<org.apache.poi.xwpf.usermodel.XWPFTable> existingTables = document.getTables();
            int tableIndex = 0;
            for (TableContent tableContent : tables) {
                org.apache.poi.xwpf.usermodel.XWPFTable table;
                List<List<List<RunContent>>> rows = tableContent.getRows();
                
                // Se existe tabela no template, usar ela; caso contrário, criar nova
                if (tableIndex < existingTables.size()) {
                    table = existingTables.get(tableIndex);
                } else {
                    // Criar nova tabela - primeiro criar um parágrafo e então a tabela
                    document.createParagraph();
                    // Determinar número máximo de colunas em todas as linhas
                    int maxCols = rows.stream()
                        .mapToInt(List::size)
                        .max()
                        .orElse(1);
                    table = document.createTable(1, maxCols);
                    // Remover a linha inicial que será substituída pelo conteúdo
                    if (!table.getRows().isEmpty()) {
                        table.removeRow(0);
                    }
                    // Aplicar formatação básica à tabela
                    applyTableFormatting(table);
                }
                
                // Processar todas as linhas do conteúdo editado
                List<org.apache.poi.xwpf.usermodel.XWPFTableRow> tableRows = table.getRows();
                
                for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                    List<List<RunContent>> editedRow = rows.get(rowIndex);
                    org.apache.poi.xwpf.usermodel.XWPFTableRow tableRow;
                    
                    // Se a linha existe na tabela, usar; caso contrário, criar
                    if (rowIndex < tableRows.size()) {
                        tableRow = tableRows.get(rowIndex);
                    } else {
                        tableRow = table.createRow();
                    }
                    
                    // Garantir que a linha tenha células suficientes
                    List<org.apache.poi.xwpf.usermodel.XWPFTableCell> cells = tableRow.getTableCells();
                    while (cells.size() < editedRow.size()) {
                        tableRow.createCell();
                        cells = tableRow.getTableCells();
                    }
                    
                    // Processar todas as células da linha editada
                    for (int cellIndex = 0; cellIndex < editedRow.size(); cellIndex++) {
                        List<RunContent> editedCell = editedRow.get(cellIndex);
                        org.apache.poi.xwpf.usermodel.XWPFTableCell cell = cells.get(cellIndex);
                        
                        // Aplicar conteúdo editado à célula com suporte a múltiplos parágrafos
                        applyCellContent(cell, editedCell);
                    }
                }
                
                // Remover linhas extras da tabela se houver mais linhas no template do que no conteúdo editado
                tableRows = table.getRows();
                while (tableRows.size() > rows.size()) {
                    table.removeRow(tableRows.size() - 1);
                    tableRows = table.getRows();
                }
                
                // Aplicar formatação final à tabela (bordas, larguras, etc.)
                applyTableFormatting(table);
                
                tableIndex++;
            }
            
            // VERIFICAÇÃO FINAL: Remover marcadores de tabela de todo o documento antes de salvar
            removeTableMarkersFromDocument(document);
            
            // Salvar documento DOCX
            document.write(outputStream);
            byte[] docxBytes = outputStream.toByteArray();
            
            // Converter DOCX para PDF
            try {
                return convertDocxToPdf(docxBytes);
            } catch (Exception e) {
                log.error("Erro ao converter DOCX para PDF: {}", e.getMessage(), e);
                throw new IOException("Erro ao converter documento para PDF: " + e.getMessage(), e);
            }
        }
    }
}

