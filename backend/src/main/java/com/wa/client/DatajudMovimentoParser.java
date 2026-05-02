package com.wa.client;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai hits e movimentos da resposta DataJud — espelha consultar_tjrj_datajud.py.
 */
public final class DatajudMovimentoParser {

    private static final ZoneId ZONA_BR = ZoneId.of("America/Sao_Paulo");

    /** Data calendar sem hora: início do dia em Brasília (alinha ao filtro da consulta). */
    private static final DateTimeFormatter ISO_DATE_BR =
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter BR_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter BR_DATA_HORA = new DateTimeFormatterBuilder()
            .appendPattern("dd/MM/uuuu HH:mm")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Índice CNJ costuma enviar {@code 2026-03-12 12:56:00} com espaço (sem {@code T});
     * {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} não aceita esse separador, e o filtro de período descartava todos.
     */
    private static final DateTimeFormatter ISO_LOCAL_SPACE_SEC =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_LOCAL_SPACE_MIN =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern ALL_DIGITS = Pattern.compile("^\\d+$");

    private static final Pattern GRAU_G_NUM = Pattern.compile("(?i)G\\s*(\\d+)");

    private DatajudMovimentoParser() {
    }

    public record DocumentHit(String id, Map<String, Object> source) {
    }

    /**
     * Extrai hits com _id e _source; descarta _id duplicado (mantem o primeiro).
     */
    @SuppressWarnings("unchecked")
    public static List<DocumentHit> parseDocumentHits(Map<String, Object> response) {
        if (response == null) {
            return List.of();
        }
        Object hitsObj = response.get("hits");
        if (!(hitsObj instanceof Map)) {
            return List.of();
        }
        Object innerHits = ((Map<String, Object>) hitsObj).get("hits");
        if (!(innerHits instanceof List<?> list)) {
            return List.of();
        }
        Map<String, DocumentHit> byId = new LinkedHashMap<>();
        for (Object hit : list) {
            if (!(hit instanceof Map)) {
                continue;
            }
            Map<String, Object> hitMap = (Map<String, Object>) hit;
            Object idObj = hitMap.get("_id");
            String id = idObj == null ? "" : String.valueOf(idObj);
            Object source = hitMap.get("_source");
            if (!(source instanceof Map)) {
                continue;
            }
            if (id.isEmpty()) {
                id = "hit-" + byId.size();
            }
            byId.putIfAbsent(id, new DocumentHit(id, (Map<String, Object>) source));
        }
        return List.copyOf(byId.values());
    }

    public static String extractGrau(Map<String, Object> source) {
        String raw = firstNonBlank(source, "grau", "Grau", "siglaGrau", "grauProcesso");
        return raw == null ? "desconhecido" : raw;
    }

    public static String extractClasseProcessual(Map<String, Object> source) {
        return firstNonBlank(source, "classeProcessual", "nomeClasse", "classe", "classeProcesso");
    }

    public static String extractOrgaoJulgador(Map<String, Object> source) {
        return firstNonBlank(source, "orgaoJulgador", "orgaoJulgadorNome", "nomeOrgao");
    }

    private static String firstNonBlank(Map<String, Object> source, String... keys) {
        if (source == null) {
            return null;
        }
        for (String k : keys) {
            Object o = source.get(k);
            if (o == null) {
                continue;
            }
            String s = String.valueOf(o).trim();
            if (!s.isEmpty()) {
                return s;
            }
        }
        return null;
    }

    /**
     * G1 antes de G2; {@code desconhecido} por ultimo.
     */
    public static int compareGrauLabel(String a, String b) {
        int oa = grauOrder(a);
        int ob = grauOrder(b);
        if (oa != ob) {
            return Integer.compare(oa, ob);
        }
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareToIgnoreCase(b);
    }

    private static int grauOrder(String g) {
        if (g == null) {
            return 10_000;
        }
        if ("desconhecido".equalsIgnoreCase(g.trim())) {
            return 9_000;
        }
        Matcher m = GRAU_G_NUM.matcher(g);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 5_000 + (Math.abs(g.hashCode()) % 1000);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseHits(Map<String, Object> response) {
        if (response == null) {
            return List.of();
        }
        Object hitsObj = response.get("hits");
        if (!(hitsObj instanceof Map)) {
            return List.of();
        }
        Object innerHits = ((Map<String, Object>) hitsObj).get("hits");
        if (!(innerHits instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object hit : list) {
            if (!(hit instanceof Map)) {
                continue;
            }
            Object source = ((Map<String, Object>) hit).get("_source");
            if (source instanceof Map) {
                out.add((Map<String, Object>) source);
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static List<ParsedMovimento> parseAndamentos(Map<String, Object> source) {
        if (source == null) {
            return List.of();
        }
        Object movObj = source.containsKey("movimentos") ? source.get("movimentos") : source.get("movimentacoes");
        if (!(movObj instanceof List<?> list)) {
            return List.of();
        }
        List<ParsedMovimento> parsed = new ArrayList<>();
        for (Object mov : list) {
            if (!(mov instanceof Map)) {
                continue;
            }
            Map<String, Object> m = (Map<String, Object>) mov;
            String data = str(m.get("dataHora"), m.get("data"));
            String nome = str(m.get("nome"), m.get("descricao"), m.get("texto"));
            String codigo = str(m.get("codigo"), m.get("id"));
            parsed.add(new ParsedMovimento(data, nome != null ? nome.trim() : "", codigo != null ? codigo.trim() : ""));
        }
        parsed.sort(Comparator.comparing(ParsedMovimento::data, Comparator.nullsLast(String::compareTo)).reversed());
        return parsed;
    }

    private static String str(Object... candidates) {
        for (Object o : candidates) {
            if (o != null) {
                String s = String.valueOf(o);
                if (!s.isBlank()) {
                    return s;
                }
            }
        }
        return "";
    }

    public static Optional<Instant> parseDataInstant(String dataStr) {
        if (dataStr == null || dataStr.isBlank()) {
            return Optional.empty();
        }
        String trimmed = dataStr.trim();

        // yyyy-MM-dd (só data): 00:00 em America/Sao_Paulo
        if (trimmed.length() == 10 && trimmed.charAt(4) == '-' && trimmed.charAt(7) == '-') {
            try {
                LocalDate d = LocalDate.parse(trimmed, ISO_DATE_BR);
                return Optional.of(d.atStartOfDay(ZONA_BR).toInstant());
            } catch (DateTimeParseException ignored) {
                // fall through
            }
        }

        // dd/MM/yyyy ou dd/MM/yyyy HH:mm
        if (trimmed.contains("/")) {
            try {
                if (trimmed.length() <= 10) {
                    LocalDate d = LocalDate.parse(trimmed, BR_DATA);
                    return Optional.of(d.atStartOfDay(ZONA_BR).toInstant());
                }
                LocalDateTime ldt = LocalDateTime.parse(trimmed, BR_DATA_HORA);
                return Optional.of(ldt.atZone(ZONA_BR).toInstant());
            } catch (DateTimeParseException ignored) {
                // fall through
            }
        }

        // yyyy-MM-dd HH:mm[:ss] com espaço (sem T) — comum no Datajud
        if (trimmed.length() >= 16 && trimmed.charAt(4) == '-' && trimmed.charAt(7) == '-'
                && trimmed.indexOf(' ') >= 10 && !trimmed.contains("T")) {
            try {
                return Optional.of(LocalDateTime.parse(trimmed, ISO_LOCAL_SPACE_SEC).atZone(ZONA_BR).toInstant());
            } catch (DateTimeParseException ignored) {
                // try minute precision
            }
            try {
                return Optional.of(LocalDateTime.parse(trimmed, ISO_LOCAL_SPACE_MIN).atZone(ZONA_BR).toInstant());
            } catch (DateTimeParseException ignored) {
                // fall through
            }
        }

        // Epoch em milissegundos (string só dígitos, 13 caracteres)
        if (trimmed.length() >= 12 && ALL_DIGITS.matcher(trimmed).matches()) {
            try {
                long ms = Long.parseLong(trimmed);
                if (ms > 1_000_000_000_000L) {
                    return Optional.of(Instant.ofEpochMilli(ms));
                }
                if (ms > 1_000_000_000L) {
                    return Optional.of(Instant.ofEpochSecond(ms));
                }
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }

        String normalized = trimmed.replace("Z", "+00:00");
        try {
            return Optional.of(OffsetDateTime.parse(normalized).toInstant());
        } catch (DateTimeParseException ignored) {
            // continue
        }
        try {
            return Optional.of(Instant.parse(trimmed));
        } catch (DateTimeParseException ignored) {
            // continue
        }
        try {
            return Optional.of(LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atOffset(java.time.ZoneOffset.UTC)
                    .toInstant());
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public record ParsedMovimento(String data, String descricao, String codigo) {
    }

    public static List<ParsedMovimento> filterByInterval(
            List<ParsedMovimento> movimentos,
            Instant inicioInclusive,
            Instant fimInclusive
    ) {
        if (movimentos.isEmpty()) {
            return List.of();
        }
        List<ParsedMovimento> out = new ArrayList<>();
        for (ParsedMovimento m : movimentos) {
            Optional<Instant> instantOpt = parseDataInstant(m.data());
            if (instantOpt.isEmpty()) {
                continue;
            }
            Instant t = instantOpt.get();
            if (!t.isBefore(inicioInclusive) && !t.isAfter(fimInclusive)) {
                out.add(m);
            }
        }
        out.sort(Comparator.comparing(ParsedMovimento::data, Comparator.nullsLast(String::compareTo)).reversed());
        return Collections.unmodifiableList(out);
    }
}
