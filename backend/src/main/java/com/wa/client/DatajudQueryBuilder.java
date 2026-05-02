package com.wa.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds Elasticsearch-style body matching consultar_tjrj_datajud.py {@code build_query}.
 */
public final class DatajudQueryBuilder {

    private DatajudQueryBuilder() {
    }

    /**
     * Default: up to 10 hits, ceiling 50 (G1+G2 + occasional duplicate).
     */
    public static Map<String, Object> buildSearchByNumeroProcesso(String numeroProcesso) {
        return buildSearchByNumeroProcesso(numeroProcesso, 10, 50);
    }

    /**
     * @param maxHits capped internally between 1 and {@code ceiling} (typ. 50)
     */
    public static Map<String, Object> buildSearchByNumeroProcesso(String numeroProcesso, int maxHits, int ceiling) {
        int cap = ceiling > 0 ? ceiling : 50;
        int size = Math.max(1, Math.min(maxHits, cap));
        String digits = (numeroProcesso == null ? "" : numeroProcesso).replaceAll("\\D", "");

        List<Map<String, Object>> should = List.of(
                Map.of("term", Map.of("numeroProcesso.keyword", numeroProcesso)),
                Map.of("term", Map.of("numeroProcesso", numeroProcesso)),
                Map.of("term", Map.of("numeroProcesso.keyword", digits)),
                Map.of("term", Map.of("numeroProcesso", digits)),
                Map.of("term", Map.of("processoNumeroFormatado.keyword", numeroProcesso)),
                Map.of("term", Map.of("processoNumeroFormatado", numeroProcesso))
        );

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("should", should);
        bool.put("minimum_should_match", 1);

        Map<String, Object> query = Map.of("bool", bool);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("size", size);
        root.put("query", query);
        return root;
    }
}
