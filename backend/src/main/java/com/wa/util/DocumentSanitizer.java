package com.wa.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Utilitário para sanitização e validação de dados de documentos
 * Protege contra XSS, injeção de código, path traversal e DoS
 */
@Slf4j
public class DocumentSanitizer {
    
    // Tamanhos máximos
    private static final int MAX_TEXT_LENGTH = 1000000; // 1MB de texto
    private static final int MAX_DELTA_OPS = 10000;
    private static final int MAX_SINGLE_INSERT_LENGTH = 100000;
    
    // Padrões permitidos para templateName
    private static final Pattern TEMPLATE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+\\.docx$");
    
    // Atributos Quill permitidos (apenas formatação)
    private static final Set<String> ALLOWED_QUILL_ATTRIBUTES = Set.of(
        "bold", "italic", "underline", "strike", "code",
        "color", "background", "size", "font", "link",
        "list", "indent", "align", "header", "script",
        "blockquote", "direction"
    );
    
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Sanitiza texto removendo caracteres perigosos e scripts
     */
    public static String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        
        // Limitar tamanho
        if (text.length() > MAX_TEXT_LENGTH) {
            log.warn("Texto muito longo, truncando: {} caracteres", text.length());
            text = text.substring(0, MAX_TEXT_LENGTH);
        }
        
        // Remover caracteres de controle (exceto quebras de linha e tabs)
        text = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        
        // Escapar caracteres especiais HTML/XML básicos
        text = text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
        
        return text;
    }
    
    /**
     * Escapa caracteres especiais para Word (menos agressivo que sanitizeText)
     */
    public static String escapeForWord(String text) {
        if (text == null) {
            return "";
        }
        
        // Limitar tamanho
        if (text.length() > MAX_TEXT_LENGTH) {
            log.warn("Texto muito longo para Word, truncando: {} caracteres", text.length());
            text = text.substring(0, MAX_TEXT_LENGTH);
        }
        
        // Remover apenas caracteres de controle perigosos
        text = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        
        return text;
    }
    
    /**
     * Valida nome do template para prevenir path traversal
     */
    public static boolean validateTemplateName(String templateName) {
        if (!StringUtils.hasText(templateName)) {
            return false;
        }
        
        // Verificar padrão permitido
        if (!TEMPLATE_NAME_PATTERN.matcher(templateName).matches()) {
            log.warn("Nome de template inválido: {}", templateName);
            return false;
        }
        
        // Verificar path traversal
        if (templateName.contains("..") || templateName.contains("/") || templateName.contains("\\")) {
            log.warn("Tentativa de path traversal detectada: {}", templateName);
            return false;
        }
        
        // Verificar se não é caminho absoluto
        if (templateName.startsWith("/") || templateName.startsWith("\\") || 
            (templateName.length() > 1 && templateName.charAt(1) == ':')) {
            log.warn("Tentativa de usar caminho absoluto: {}", templateName);
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida estrutura do Quill Delta
     */
    public static boolean validateQuillDelta(Object delta) {
        if (delta == null) {
            return false;
        }
        
        try {
            JsonNode deltaNode;
            if (delta instanceof JsonNode) {
                deltaNode = (JsonNode) delta;
            } else {
                deltaNode = objectMapper.valueToTree(delta);
            }
            
            // Verificar se é objeto
            if (!deltaNode.isObject()) {
                log.warn("Delta não é um objeto JSON");
                return false;
            }
            
            // Verificar se tem array 'ops'
            if (!deltaNode.has("ops") || !deltaNode.get("ops").isArray()) {
                log.warn("Delta não contém array 'ops' válido");
                return false;
            }
            
            JsonNode ops = deltaNode.get("ops");
            
            // Limitar número de operações
            if (ops.size() > MAX_DELTA_OPS) {
                log.warn("Delta contém muitas operações: {}", ops.size());
                return false;
            }
            
            // Validar cada operação
            for (JsonNode op : ops) {
                if (!validateQuillOperation(op)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Erro ao validar Quill Delta: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Valida uma operação individual do Quill Delta
     */
    private static boolean validateQuillOperation(JsonNode op) {
        if (!op.isObject()) {
            log.warn("Operação não é um objeto JSON");
            return false;
        }
        
        // Verificar tipo de operação
        boolean hasInsert = op.has("insert");
        boolean hasRetain = op.has("retain");
        boolean hasDelete = op.has("delete");
        
        int operationCount = (hasInsert ? 1 : 0) + (hasRetain ? 1 : 0) + (hasDelete ? 1 : 0);
        
        if (operationCount != 1) {
            log.warn("Operação deve ter exatamente um tipo (insert, retain ou delete)");
            return false;
        }
        
        // Validar insert
        if (hasInsert) {
            JsonNode insert = op.get("insert");
            if (insert.isTextual()) {
                String text = insert.asText();
                if (text.length() > MAX_SINGLE_INSERT_LENGTH) {
                    log.warn("Insert muito longo: {} caracteres", text.length());
                    return false;
                }
            } else if (!insert.isObject()) {
                // Pode ser objeto para embeds (imagens, etc.) - rejeitar por segurança
                log.warn("Insert não é texto nem objeto válido");
                return false;
            }
        }
        
        // Validar retain
        if (hasRetain) {
            JsonNode retain = op.get("retain");
            if (!retain.isNumber() || retain.asInt() < 0) {
                log.warn("Retain deve ser um número positivo");
                return false;
            }
        }
        
        // Validar delete
        if (hasDelete) {
            JsonNode delete = op.get("delete");
            if (!delete.isNumber() || delete.asInt() < 0) {
                log.warn("Delete deve ser um número positivo");
                return false;
            }
        }
        
        // Validar atributos
        if (op.has("attributes")) {
            JsonNode attributes = op.get("attributes");
            if (!attributes.isObject()) {
                log.warn("Attributes deve ser um objeto");
                return false;
            }
            
            Iterator<Map.Entry<String, JsonNode>> fields = attributes.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String attrName = field.getKey();
                JsonNode attrValue = field.getValue();
                
                // Verificar se atributo é permitido
                if (!ALLOWED_QUILL_ATTRIBUTES.contains(attrName)) {
                    log.warn("Atributo não permitido: {}", attrName);
                    return false;
                }
                
                // Validar valores específicos
                if (!validateAttributeValue(attrName, attrValue)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Valida valor de um atributo específico
     */
    private static boolean validateAttributeValue(String attrName, JsonNode attrValue) {
        switch (attrName) {
            case "color":
            case "background":
                // Cores devem ser strings válidas (hex ou nome)
                if (!attrValue.isTextual()) {
                    return false;
                }
                String color = attrValue.asText();
                // Validar formato básico (hex ou nome de cor)
                return color.matches("^#?[0-9A-Fa-f]{6}$|^[a-zA-Z]+$") && color.length() <= 20;
                
            case "size":
                // Tamanho deve ser string com formato válido (ex: "small", "large", "12px")
                if (!attrValue.isTextual()) {
                    return false;
                }
                return attrValue.asText().length() <= 20;
                
            case "font":
                // Fonte deve ser string válida
                if (!attrValue.isTextual()) {
                    return false;
                }
                return attrValue.asText().length() <= 50;
                
            case "link":
                // Link deve ser URL válida (básico)
                if (!attrValue.isTextual()) {
                    return false;
                }
                String link = attrValue.asText();
                return link.length() <= 2000 && 
                       (link.startsWith("http://") || link.startsWith("https://") || link.startsWith("/"));
                
            case "list":
                // Lista deve ser "ordered" ou "bullet"
                if (!attrValue.isTextual()) {
                    return false;
                }
                String listType = attrValue.asText();
                return "ordered".equals(listType) || "bullet".equals(listType);
                
            case "indent":
                // Indentação deve ser número entre 1 e 10
                if (!attrValue.isNumber()) {
                    return false;
                }
                int indent = attrValue.asInt();
                return indent >= 1 && indent <= 10;
                
            case "align":
                // Alinhamento deve ser válido
                if (!attrValue.isTextual()) {
                    return false;
                }
                String align = attrValue.asText();
                return Set.of("left", "center", "right", "justify").contains(align);
                
            case "header":
                // Header deve ser número entre 1 e 6
                if (!attrValue.isNumber()) {
                    return false;
                }
                int header = attrValue.asInt();
                return header >= 1 && header <= 6;
                
            case "script":
                // Script deve ser "sub" ou "super"
                if (!attrValue.isTextual()) {
                    return false;
                }
                String script = attrValue.asText();
                return "sub".equals(script) || "super".equals(script);
                
            case "direction":
                // Direção deve ser "rtl"
                if (!attrValue.isTextual()) {
                    return false;
                }
                return "rtl".equals(attrValue.asText());
                
            case "bold":
            case "italic":
            case "underline":
            case "strike":
            case "code":
            case "blockquote":
                // Valores booleanos
                return attrValue.isBoolean() || attrValue.isNumber();
                
            default:
                // Outros atributos devem ser valores simples
                return attrValue.isTextual() || attrValue.isNumber() || attrValue.isBoolean();
        }
    }
    
    /**
     * Sanitiza conteúdo do Quill Delta mantendo formatação válida
     */
    public static JsonNode sanitizeQuillDelta(Object delta) {
        if (!validateQuillDelta(delta)) {
            throw new IllegalArgumentException("Delta inválido não pode ser sanitizado");
        }
        
        try {
            JsonNode deltaNode;
            if (delta instanceof JsonNode) {
                deltaNode = (JsonNode) delta;
            } else {
                deltaNode = objectMapper.valueToTree(delta);
            }
            
            // Criar novo objeto para resultado sanitizado
            ObjectMapper mapper = new ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode sanitized = mapper.createObjectNode();
            com.fasterxml.jackson.databind.node.ArrayNode sanitizedOps = mapper.createArrayNode();
            
            JsonNode ops = deltaNode.get("ops");
            for (JsonNode op : ops) {
                com.fasterxml.jackson.databind.node.ObjectNode sanitizedOp = sanitizeOperation(op, mapper);
                sanitizedOps.add(sanitizedOp);
            }
            
            sanitized.set("ops", sanitizedOps);
            return sanitized;
        } catch (Exception e) {
            log.error("Erro ao sanitizar Quill Delta: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Erro ao sanitizar Delta", e);
        }
    }
    
    /**
     * Sanitiza uma operação individual
     */
    private static com.fasterxml.jackson.databind.node.ObjectNode sanitizeOperation(
            JsonNode op, ObjectMapper mapper) {
        com.fasterxml.jackson.databind.node.ObjectNode sanitizedOp = mapper.createObjectNode();
        
        // Copiar operação (insert, retain ou delete)
        if (op.has("insert")) {
            JsonNode insert = op.get("insert");
            if (insert.isTextual()) {
                // Sanitizar texto
                String text = escapeForWord(insert.asText());
                sanitizedOp.put("insert", text);
            } else {
                // Manter objeto (mas não permitir embeds perigosos)
                sanitizedOp.set("insert", insert);
            }
        } else if (op.has("retain")) {
            sanitizedOp.put("retain", op.get("retain").asInt());
        } else if (op.has("delete")) {
            sanitizedOp.put("delete", op.get("delete").asInt());
        }
        
        // Sanitizar atributos
        if (op.has("attributes")) {
            JsonNode attributes = op.get("attributes");
            com.fasterxml.jackson.databind.node.ObjectNode sanitizedAttrs = mapper.createObjectNode();
            
            Iterator<Map.Entry<String, JsonNode>> fields = attributes.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String attrName = field.getKey();
                JsonNode attrValue = field.getValue();
                
                // Apenas copiar atributos permitidos e validados
                if (ALLOWED_QUILL_ATTRIBUTES.contains(attrName) && 
                    validateAttributeValue(attrName, attrValue)) {
                    sanitizedAttrs.set(attrName, attrValue);
                }
            }
            
            if (sanitizedAttrs.size() > 0) {
                sanitizedOp.set("attributes", sanitizedAttrs);
            }
        }
        
        return sanitizedOp;
    }
}

