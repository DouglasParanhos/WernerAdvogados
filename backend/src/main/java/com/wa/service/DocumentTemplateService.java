package com.wa.service;

import com.wa.dto.DocumentTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class DocumentTemplateService {
    
    private static final String DOCUMENTS_PATH = "classpath:documents/";
    private final ResourceLoader resourceLoader;
    
    public DocumentTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    /**
     * Escaneia todos os templates disponíveis e identifica seu tipo
     */
    public List<DocumentTemplateDTO> scanTemplates() {
        List<DocumentTemplateDTO> templates = new ArrayList<>();
        
        try {
            Resource resource = resourceLoader.getResource(DOCUMENTS_PATH);
            
            // Tentar acessar como diretório (funciona em desenvolvimento)
            if (resource.exists()) {
                try {
                    if (resource.getFile().isDirectory()) {
                        Path documentsDir = resource.getFile().toPath();
                        scanDirectory(documentsDir, templates);
                        // Se encontrou templates no diretório, retornar
                        if (!templates.isEmpty()) {
                            return templates;
                        }
                    }
                } catch (IOException e) {
                    // Se falhar ao acessar como File, continuar para escanear classpath
                    log.debug("Não foi possível acessar como File, tentando classpath: {}", e.getMessage());
                }
            }
            
            // Sempre tentar escanear classpath (funciona tanto em desenvolvimento quanto em JAR)
            scanClasspathResources(templates);
            
            if (templates.isEmpty()) {
                log.warn("Nenhum template encontrado. Verifique se os arquivos .docx estão em src/main/resources/documents/");
            } else {
                log.info("Encontrados {} templates", templates.size());
            }
        } catch (Exception e) {
            log.error("Erro ao escanear templates: {}", e.getMessage(), e);
        }
        
        return templates;
    }
    
    /**
     * Escaneia diretório físico
     */
    private void scanDirectory(Path documentsDir, List<DocumentTemplateDTO> templates) {
        try (Stream<Path> paths = Files.walk(documentsDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().toLowerCase().endsWith(".docx"))
                 .forEach(path -> {
                     String fileName = path.getFileName().toString();
                     Path relativePath = documentsDir.relativize(path);
                     String relativePathStr = relativePath.toString().replace("\\", "/");
                     addTemplateToList(fileName, relativePathStr, templates);
                 });
        } catch (IOException e) {
            log.error("Erro ao escanear diretório: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Escaneia recursos do classpath (para JAR)
     */
    private void scanClasspathResources(List<DocumentTemplateDTO> templates) {
        try {
            // Usar PathMatchingResourcePatternResolver para listar recursos do classpath
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            
            // Tentar diferentes padrões de busca
            String[] patterns = {
                "classpath*:documents/**/*.docx",
                "classpath:documents/**/*.docx",
                "classpath*:documents/*.docx",
                "classpath:documents/*.docx"
            };
            
            for (String pattern : patterns) {
                try {
                    Resource[] resources = resolver.getResources(pattern);
                    log.debug("Padrão '{}' encontrou {} recursos", pattern, resources.length);
                    
                    for (Resource resource : resources) {
                        try {
                            String uri = resource.getURI().toString();
                            String fileName = resource.getFilename();
                            if (fileName != null && fileName.toLowerCase().endsWith(".docx")) {
                                // Extrair caminho relativo
                                String relativePath = fileName;
                                if (uri.contains("/documents/")) {
                                    int startIdx = uri.indexOf("/documents/") + "/documents/".length();
                                    String pathAfterDocuments = uri.substring(startIdx);
                                    // Remover query parameters se houver
                                    if (pathAfterDocuments.contains("?")) {
                                        pathAfterDocuments = pathAfterDocuments.substring(0, pathAfterDocuments.indexOf("?"));
                                    }
                                    // Remover protocolo/file: se houver
                                    if (pathAfterDocuments.contains("!/")) {
                                        pathAfterDocuments = pathAfterDocuments.substring(pathAfterDocuments.indexOf("!/") + 2);
                                    }
                                    relativePath = pathAfterDocuments;
                                }
                                // Se não tem subpasta, usar apenas o nome do arquivo
                                if (!relativePath.contains("/") && !relativePath.contains("\\")) {
                                    relativePath = fileName;
                                }
                                // Normalizar separadores de caminho
                                relativePath = relativePath.replace("\\", "/");
                                addTemplateToList(fileName, relativePath, templates);
                            }
                        } catch (Exception e) {
                            log.debug("Erro ao processar recurso: {}", e.getMessage());
                        }
                    }
                    
                    // Se encontrou recursos, não precisa tentar outros padrões
                    if (resources.length > 0) {
                        break;
                    }
                } catch (Exception e) {
                    log.debug("Erro ao buscar com padrão '{}': {}", pattern, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Erro ao escanear recursos do classpath: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Adiciona template à lista
     */
    private void addTemplateToList(String fileName, String relativePath, List<DocumentTemplateDTO> templates) {
        String type = identifyDocumentType(fileName, relativePath);
        String category = identifyCategory(relativePath);
        
        DocumentTemplateDTO template = new DocumentTemplateDTO();
        template.setFileName(fileName);
        String formattedName = formatTemplateName(fileName);
        template.setName(formattedName);
        template.setType(type);
        template.setCategory(category);
        template.setRelativePath(relativePath);
        log.debug("Template adicionado: fileName='{}', formattedName='{}'", fileName, formattedName);
        templates.add(template);
    }
    
    /**
     * Filtra templates por tipo de processo
     */
    public List<DocumentTemplateDTO> getTemplatesByProcessType(String tipoProcesso) {
        List<DocumentTemplateDTO> allTemplates = scanTemplates();
        
        if (tipoProcesso == null || tipoProcesso.trim().isEmpty()) {
            log.debug("Tipo de processo não informado, retornando lista vazia");
            return new ArrayList<>();
        }
        
        String normalizedType = normalizeProcessType(tipoProcesso);
        log.debug("Filtrando templates por tipo: '{}' (normalizado: '{}')", tipoProcesso, normalizedType);
        
        List<DocumentTemplateDTO> filteredTemplates = allTemplates.stream()
                .filter(template -> {
                    boolean matches = template.getType().equalsIgnoreCase(normalizedType);
                    log.debug("Template '{}' tipo '{}' corresponde a '{}': {}", 
                             template.getFileName(), template.getType(), normalizedType, matches);
                    return matches;
                })
                .toList();
        
        log.debug("Encontrados {} templates para o tipo '{}' (de {} templates totais)", 
                 filteredTemplates.size(), normalizedType, allTemplates.size());
        return filteredTemplates;
    }
    
    /**
     * Identifica o tipo de documento baseado no nome do arquivo e caminho
     */
    private String identifyDocumentType(String fileName, String relativePath) {
        String lowerFileName = fileName.toLowerCase();
        String lowerPath = relativePath != null ? relativePath.toLowerCase() : "";
        
        // Documentos em subpastas especiais podem ter tipos mistos
        if (lowerPath.contains("documentos/") || lowerPath.contains("iniciais/")) {
            // Ainda identificar tipo de processo se presente no nome
            if (lowerFileName.contains("interniveis")) {
                return "INTERNIVEIS";
            } else if (lowerFileName.contains("piso")) {
                return "PISO";
            } else if (lowerFileName.contains("nova_escola") || 
                       lowerFileName.contains("novaescola") ||
                       lowerFileName.contains("_ne") ||
                       (lowerFileName.contains("ne") && !lowerFileName.contains("interniveis"))) {
                return "NOVAESCOLA";
            }
            // Se não tem tipo específico, retornar genérico
            return "GENERIC";
        }
        
        // Documentos na raiz - identificar tipo normalmente
        if (lowerFileName.contains("interniveis")) {
            return "INTERNIVEIS";
        } else if (lowerFileName.contains("piso")) {
            return "PISO";
        } else if (lowerFileName.contains("nova_escola") || 
                   lowerFileName.contains("novaescola") ||
                   lowerFileName.contains("_ne") ||
                   (lowerFileName.contains("ne") && !lowerFileName.contains("interniveis"))) {
            return "NOVAESCOLA";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Identifica a categoria do documento (PROCESS ou CLIENT)
     */
    private String identifyCategory(String relativePath) {
        if (relativePath == null) {
            return "PROCESS";
        }
        
        String lowerPath = relativePath.toLowerCase();
        
        // Documentos em subpastas documentos/ e iniciais/ são por cliente
        if (lowerPath.contains("documentos/") || lowerPath.contains("iniciais/")) {
            return "CLIENT";
        }
        
        // Documentos na raiz são por processo
        return "PROCESS";
    }
    
    /**
     * Normaliza o tipo de processo para comparação
     */
    private String normalizeProcessType(String tipoProcesso) {
        if (tipoProcesso == null) {
            return null;
        }
        
        String normalized = tipoProcesso.toUpperCase().trim();
        
        // Mapear variações comuns - ordem importa (verificar casos mais específicos primeiro)
        if (normalized.contains("INTERNIVEIS") || normalized.contains("INTERNÍVEIS")) {
            return "INTERNIVEIS";
        }
        
        if (normalized.contains("PISO")) {
            return "PISO";
        }
        
        if (normalized.contains("NOVA") && normalized.contains("ESCOLA")) {
            return "NOVAESCOLA";
        }
        
        // Se já está em um dos formatos esperados, retornar como está
        if (normalized.equals("PISO") || normalized.equals("INTERNIVEIS") || normalized.equals("NOVAESCOLA")) {
            return normalized;
        }
        
        return normalized;
    }
    
    /**
     * Formata o nome do template para exibição
     */
    private String formatTemplateName(String fileName) {
        // Remove extensão
        String name = fileName.replace(".docx", "");
        String originalName = name;
        
        // Substitui underscores e hífens por espaços
        name = name.replace("_", " ").replace("-", " ");
        
        // Adicionar espaços antes de padrões conhecidos (para palavras concatenadas)
        name = addSpacesBeforePatterns(name);
        
        // Expandir abreviações comuns
        name = expandAbbreviations(name);
        
        // Capitaliza primeira letra de cada palavra
        String[] words = name.split("\\s+");
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();
            if (!word.isEmpty()) {
                if (formatted.length() > 0) {
                    formatted.append(" ");
                }
                // Capitalizar primeira letra, resto em minúsculas
                if (word.length() > 1) {
                    formatted.append(word.substring(0, 1).toUpperCase())
                             .append(word.substring(1).toLowerCase());
                } else {
                    formatted.append(word.toUpperCase());
                }
            }
        }
        
        String result = formatted.toString();
        // Log sempre para debug
        log.info("Formatando nome: '{}' -> '{}'", originalName, result);
        return result;
    }
    
    /**
     * Adiciona espaços antes de padrões conhecidos para separar palavras concatenadas
     */
    private String addSpacesBeforePatterns(String name) {
        String result = name.toLowerCase();
        
        // Casos específicos comuns - ordem importa (mais específicos primeiro)
        // Procuracao
        result = result.replace("procuracaointernepiso", "procuracao inter ne piso");
        result = result.replace("procuracaointerpiso", "procuracao inter piso");
        result = result.replace("procuracaointerne", "procuracao inter ne");
        result = result.replace("procuracaointerniveis", "procuracao interniveis");
        result = result.replace("procuracaonepiso", "procuracao ne piso");
        result = result.replace("procuracaointer", "procuracao inter");
        result = result.replace("procuracaopiso", "procuracao piso");
        result = result.replace("procuracaone", "procuracao ne");
        
        // Contrato
        result = result.replace("contratointnepiso", "contrato int ne piso");
        result = result.replace("contratointpiso", "contrato int piso");
        result = result.replace("contratointne", "contrato int ne");
        result = result.replace("contratonepiso", "contrato ne piso");
        result = result.replace("contratoint", "contrato int");
        result = result.replace("contratopiso", "contrato piso");
        result = result.replace("contratone", "contrato ne");
        
        // Declaracao
        result = result.replace("declaracaohipo", "declaracao hipo");
        
        // Peticao
        result = result.replace("peticaoinicialinterniveis", "peticao inicial interniveis");
        result = result.replace("peticaoinicialnovaescola", "peticao inicial nova escola");
        result = result.replace("peticaoinicialpisovc", "peticao inicial piso vc");
        
        // Adicionar espaços antes de padrões gerais usando regex (apenas se ainda não foram separados)
        result = result.replaceAll("([a-z])(ne)([a-z])", "$1 ne $3");
        result = result.replaceAll("([a-z])(int)([a-z])", "$1 int $3");
        result = result.replaceAll("([a-z])(piso)([a-z])", "$1 piso $3");
        result = result.replaceAll("([a-z])(procuracao)", "$1 procuracao");
        result = result.replaceAll("([a-z])(contrato)", "$1 contrato");
        result = result.replaceAll("([a-z])(peticao)", "$1 peticao");
        result = result.replaceAll("([a-z])(declaracao)", "$1 declaracao");
        result = result.replaceAll("([a-z])(inicial)", "$1 inicial");
        result = result.replaceAll("([a-z])(hipo)", "$1 hipo");
        result = result.replaceAll("([a-z])(interniveis)", "$1 interniveis");
        
        // Limpar espaços múltiplos
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }
    
    /**
     * Expande abreviações comuns nos nomes de documentos
     * Retorna palavras em minúsculas para serem capitalizadas depois
     */
    private String expandAbbreviations(String name) {
        String expanded = name.toLowerCase();
        
        // Primeiro, expandir palavras compostas que podem vir com camelCase (ex: NovaEscola, Interniveis)
        // Essas palavras já foram separadas por espaços, mas precisam ser expandidas
        expanded = expanded.replaceAll("\\bnovaescola\\b", "nova escola");
        expanded = expanded.replaceAll("\\bnova escola\\b", "nova escola");
        expanded = expanded.replaceAll("\\binterniveis\\b", "interníveis");
        expanded = expanded.replaceAll("\\binterníveis\\b", "interníveis");
        // Corrigir acentuação de Hipossuficiência (pode vir sem acento do nome do arquivo)
        // Usar replace simples para garantir que funcione mesmo quando a palavra está isolada
        // IMPORTANTE: fazer isso ANTES de outras substituições que possam interferir
        expanded = expanded.replace("hipossuficiencia", "hipossuficiência");
        
        // Abreviações de tipos de processo
        expanded = expanded.replaceAll("\\bne\\b", "nova escola");
        expanded = expanded.replaceAll("\\bint\\b", "interníveis");
        expanded = expanded.replaceAll("\\bpiso\\b", "piso");
        
        // Abreviações de tipos de documentos
        expanded = expanded.replaceAll("\\bprocuracao\\b", "procuração");
        expanded = expanded.replaceAll("\\bcontrato\\b", "contrato");
        expanded = expanded.replaceAll("\\bpeticao\\b", "petição");
        expanded = expanded.replaceAll("\\binicial\\b", "inicial");
        expanded = expanded.replaceAll("\\bdeclaracao\\b", "declaração");
        // Corrigir Hipossuficiência (pode vir sem acento do nome do arquivo)
        expanded = expanded.replace("hipossuficiencia", "hipossuficiência");
        expanded = expanded.replaceAll("\\bhipo\\b", "hipossuficiência");
        expanded = expanded.replaceAll("\\bcr\\b", "contrarrazões");
        expanded = expanded.replaceAll("\\bcontrarrazões\\b", "contrarrazões");
        expanded = expanded.replaceAll("\\bcontrarrazoes\\b", "contrarrazões");
        expanded = expanded.replaceAll("\\beds\\b", "embargos de declaração");
        expanded = expanded.replaceAll("\\bembargos\\b", "embargos");
        expanded = expanded.replaceAll("\\bagravo\\b", "agravo");
        expanded = expanded.replaceAll("\\binterno\\b", "interno");
        expanded = expanded.replaceAll("\\binstrumento\\b", "instrumento");
        expanded = expanded.replaceAll("\\bparametro\\b", "parâmetro");
        expanded = expanded.replaceAll("\\bparametros\\b", "parâmetros");
        expanded = expanded.replaceAll("\\brpv\\b", "rpv");
        expanded = expanded.replaceAll("\\bvalores\\b", "valores");
        expanded = expanded.replaceAll("\\blevantamento\\b", "levantamento");
        expanded = expanded.replaceAll("\\bsuspensao\\b", "suspensão");
        expanded = expanded.replaceAll("\\bprovas\\b", "provas");
        expanded = expanded.replaceAll("\\bresposta\\b", "resposta");
        expanded = expanded.replaceAll("\\bimpugnacao\\b", "impugnação");
        expanded = expanded.replaceAll("\\berj\\b", "erj");
        expanded = expanded.replaceAll("\\bconcordando\\b", "concordando");
        expanded = expanded.replaceAll("\\bcalculos\\b", "cálculos");
        expanded = expanded.replaceAll("\\bcontador\\b", "contador");
        expanded = expanded.replaceAll("\\bdescabimento\\b", "descabimento");
        expanded = expanded.replaceAll("\\bavaliacao\\b", "avaliação");
        expanded = expanded.replaceAll("\\baplicada\\b", "aplicada");
        expanded = expanded.replaceAll("\\binf\\b", "informando");
        expanded = expanded.replaceAll("\\bsobre\\b", "sobre");
        expanded = expanded.replaceAll("\\btema\\b", "tema");
        expanded = expanded.replaceAll("\\bstj\\b", "stj");
        expanded = expanded.replaceAll("\\bresp\\b", "resp");
        expanded = expanded.replaceAll("\\bre\\b", "re");
        expanded = expanded.replaceAll("\\bapelacao\\b", "apelação");
        expanded = expanded.replaceAll("\\balegacoes\\b", "alegações");
        expanded = expanded.replaceAll("\\bfinais\\b", "finais");
        expanded = expanded.replaceAll("\\breplica\\b", "réplica");
        expanded = expanded.replaceAll("\\bgrerj\\b", "grerj");
        expanded = expanded.replaceAll("\\breembolso\\b", "reembolso");
        expanded = expanded.replaceAll("\\bjg\\b", "jg");
        expanded = expanded.replaceAll("\\bjgsuc\\b", "jgsuc");
        expanded = expanded.replaceAll("\\bsucumbenciais\\b", "sucumbenciais");
        expanded = expanded.replaceAll("\\bhonorarios\\b", "honorários");
        expanded = expanded.replaceAll("\\brenuncia\\b", "renúncia");
        expanded = expanded.replaceAll("\\bvc\\b", "vc");
        expanded = expanded.replaceAll("\\binter\\b", "interníveis");
        
        // Limpar espaços múltiplos
        expanded = expanded.replaceAll("\\s+", " ").trim();
        
        return expanded;
    }
    
    /**
     * Filtra templates por categoria (PROCESS ou CLIENT)
     */
    public List<DocumentTemplateDTO> getTemplatesByCategory(String category) {
        List<DocumentTemplateDTO> allTemplates = scanTemplates();
        
        if (category == null || category.trim().isEmpty()) {
            log.debug("Categoria não informada, retornando lista vazia");
            return new ArrayList<>();
        }
        
        String normalizedCategory = category.toUpperCase().trim();
        log.debug("Filtrando templates por categoria: '{}'", normalizedCategory);
        
        List<DocumentTemplateDTO> filteredTemplates = allTemplates.stream()
                .filter(template -> {
                    boolean matches = template.getCategory() != null && 
                                   template.getCategory().equalsIgnoreCase(normalizedCategory);
                    log.debug("Template '{}' categoria '{}' corresponde a '{}': {}", 
                             template.getFileName(), template.getCategory(), normalizedCategory, matches);
                    return matches;
                })
                .toList();
        
        log.debug("Encontrados {} templates para a categoria '{}' (de {} templates totais)", 
                 filteredTemplates.size(), normalizedCategory, allTemplates.size());
        return filteredTemplates;
    }
    
    /**
     * Verifica se um template existe
     */
    public boolean templateExists(String templateName) {
        try {
            // Tentar primeiro com caminho completo
            Resource resource = resourceLoader.getResource(DOCUMENTS_PATH + templateName);
            if (resource.exists()) {
                return true;
            }
            // Tentar apenas com nome do arquivo (na raiz)
            String fileName = templateName.contains("/") ? 
                templateName.substring(templateName.lastIndexOf("/") + 1) : templateName;
            resource = resourceLoader.getResource(DOCUMENTS_PATH + fileName);
            return resource.exists();
        } catch (Exception e) {
            log.error("Erro ao verificar template: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtém o caminho completo do template
     */
    public Resource getTemplateResource(String templateName) {
        // Se templateName já contém caminho relativo, usar diretamente
        if (templateName.contains("/")) {
            return resourceLoader.getResource(DOCUMENTS_PATH + templateName);
        }
        // Caso contrário, procurar em todos os templates para encontrar o caminho correto
        List<DocumentTemplateDTO> allTemplates = scanTemplates();
        for (DocumentTemplateDTO template : allTemplates) {
            if (template.getFileName().equals(templateName)) {
                return resourceLoader.getResource(DOCUMENTS_PATH + template.getRelativePath());
            }
        }
        // Fallback: tentar na raiz
        return resourceLoader.getResource(DOCUMENTS_PATH + templateName);
    }
}

