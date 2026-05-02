package com.wa.util;

import com.wa.model.Person;
import com.wa.model.Process;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Monta o nome base (sem extensão) para ficheiros Word gerados:
 * {@code Peticao_Cliente_NumeroProcesso} ou {@code Peticao_Cliente}.
 */
public final class GeneratedDocumentFileNameBuilder {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[<>:\"/\\\\|?*\\x00-\\x1f]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern DOCX_SUFFIX = Pattern.compile("\\.docx$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UNDERSCORE_RUNS = Pattern.compile("_+");

    private GeneratedDocumentFileNameBuilder() {}

    public static String buildForProcess(String templateName, Process process) {
        String petition = petitionPartFromTemplate(templateName);
        String client = clientPart(process.getMatriculation().getPerson().getFullname());
        String numero = safeFileToken(process.getNumero());
        return joinParts(petition, client, numero);
    }

    public static String buildForClient(String templateName, Person person) {
        String petition = petitionPartFromTemplate(templateName);
        String client = clientPart(person.getFullname());
        return joinParts(petition, client);
    }

    static String petitionPartFromTemplate(String templateName) {
        if (templateName == null || templateName.isBlank()) {
            return "Documento";
        }
        String path = templateName.replace('\\', '/').trim();
        int slash = path.lastIndexOf('/');
        String base = slash >= 0 ? path.substring(slash + 1) : path;
        base = DOCX_SUFFIX.matcher(base).replaceFirst("");
        if (base.isBlank()) {
            return "Documento";
        }
        String[] tokens = base.split("[_\\-]+");
        List<String> parts = new ArrayList<>();
        for (String t : tokens) {
            if (t == null || t.isBlank()) {
                continue;
            }
            String norm = normalizeAscii(t.trim());
            if (norm.isEmpty()) {
                continue;
            }
            parts.add(capitalizeWord(norm));
        }
        if (parts.isEmpty()) {
            return "Documento";
        }
        return String.join("_", parts);
    }

    static String clientPart(String fullname) {
        if (fullname == null || fullname.isBlank()) {
            return "Cliente";
        }
        String n = normalizeAscii(fullname.trim());
        n = WHITESPACE.matcher(n).replaceAll("_");
        n = UNDERSCORE_RUNS.matcher(n).replaceAll("_");
        n = stripEdgeUnderscores(n);
        return n.isBlank() ? "Cliente" : n;
    }

    static String safeFileToken(String raw) {
        if (raw == null || raw.isBlank()) {
            return "Sem_numero";
        }
        String n = normalizeAscii(raw.trim());
        n = INVALID_FILENAME_CHARS.matcher(n).replaceAll("_");
        n = UNDERSCORE_RUNS.matcher(n).replaceAll("_");
        n = stripEdgeUnderscores(n);
        return n.isBlank() ? "Sem_numero" : n;
    }

    static String normalizeAscii(String s) {
        if (s == null) {
            return "";
        }
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        return DIACRITICS.matcher(n).replaceAll("");
    }

    private static String capitalizeWord(String word) {
        if (word.isEmpty()) {
            return "";
        }
        if (word.length() == 1) {
            return word.toUpperCase();
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    private static String stripEdgeUnderscores(String s) {
        int start = 0;
        int end = s.length();
        while (start < end && s.charAt(start) == '_') {
            start++;
        }
        while (end > start && s.charAt(end - 1) == '_') {
            end--;
        }
        return s.substring(start, end);
    }

    private static String joinParts(String... parts) {
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                list.add(p);
            }
        }
        return list.isEmpty() ? "Documento" : String.join("_", list);
    }
}
