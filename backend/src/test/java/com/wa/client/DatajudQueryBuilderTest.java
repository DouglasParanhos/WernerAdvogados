package com.wa.client;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DatajudQueryBuilderTest {

    @Test
    void buildSearch_containsBoolShouldAndDefaultSize() {
        Map<String, Object> q = DatajudQueryBuilder.buildSearchByNumeroProcesso("0059197-75.2023.8.19.0000");

        assertThat(q.get("size")).isEqualTo(10);
        @SuppressWarnings("unchecked")
        Map<String, Object> query = (Map<String, Object>) q.get("query");
        @SuppressWarnings("unchecked")
        Map<String, Object> bool = (Map<String, Object>) query.get("bool");
        assertThat(bool.get("minimum_should_match")).isEqualTo(1);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> should = (List<Map<String, Object>>) bool.get("should");
        assertThat(should).hasSize(6);

        String digits = "00591977520238190000";
        assertThat(should).anySatisfy(term -> termContains(term, "numeroProcesso.keyword", digits));
    }

    @Test
    void buildSearch_respeitaMaxHitsECeiling() {
        Map<String, Object> q = DatajudQueryBuilder.buildSearchByNumeroProcesso("0059197-75.2023.8.19.0000", 25, 20);
        assertThat(q.get("size")).isEqualTo(20);
    }

    @SuppressWarnings("unchecked")
    private boolean termContains(Map<String, Object> termWrapper, String field, String value) {
        Map<String, Object> term = (Map<String, Object>) termWrapper.get("term");
        return term != null && value.equals(term.get(field));
    }
}
