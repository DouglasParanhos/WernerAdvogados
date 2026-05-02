package com.wa.client;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DatajudMovimentoParserTest {

    @Test
    void parseHits_extractsSource() {
        Map<String, Object> hit = Map.of("_id", "h1", "_source", Map.of("numeroProcesso", "x"));
        Map<String, Object> root = Map.of("hits", Map.of("hits", List.of(hit)));

        List<Map<String, Object>> sources = DatajudMovimentoParser.parseHits(root);
        assertThat(sources).hasSize(1);
        assertThat(sources.get(0).get("numeroProcesso")).isEqualTo("x");
    }

    @Test
    void parseAndamentos_sortsDescendingByData() {
        Map<String, Object> source = new HashMap<>();
        source.put(
                "movimentos",
                List.of(
                        Map.of("dataHora", "2026-04-01T10:00:00Z", "nome", "A", "codigo", "1"),
                        Map.of("dataHora", "2026-04-10T10:00:00Z", "nome", "B", "codigo", "2")
                ));

        List<DatajudMovimentoParser.ParsedMovimento> list = DatajudMovimentoParser.parseAndamentos(source);
        assertThat(list.get(0).data()).contains("2026-04-10");
        assertThat(list.get(1).data()).contains("2026-04-01");
    }

    @Test
    void filterByInterval_keepsOnlyInside() {
        Instant start = LocalDate.of(2026, 4, 5).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = LocalDate.of(2026, 4, 20).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<DatajudMovimentoParser.ParsedMovimento> movs = List.of(
                new DatajudMovimentoParser.ParsedMovimento("2026-04-01T12:00:00Z", "fora", "0"),
                new DatajudMovimentoParser.ParsedMovimento("2026-04-15T12:00:00Z", "dentro", "1"),
                new DatajudMovimentoParser.ParsedMovimento("2026-05-01T12:00:00Z", "fora2", "2")
        );

        List<DatajudMovimentoParser.ParsedMovimento> out =
                DatajudMovimentoParser.filterByInterval(movs, start, end);

        assertThat(out).hasSize(1);
        assertThat(out.get(0).descricao()).isEqualTo("dentro");
    }

    @Test
    void parseDocumentHits_dedupPorId() {
        Map<String, Object> src = Map.of("grau", "G1", "movimentos", List.of());
        Map<String, Object> root = Map.of(
                "hits",
                Map.of(
                        "hits",
                        List.of(
                                Map.of("_id", "dup", "_source", src),
                                Map.of("_id", "dup", "_source", Map.of("grau", "G2")))));

        List<DatajudMovimentoParser.DocumentHit> hits = DatajudMovimentoParser.parseDocumentHits(root);
        assertThat(hits).hasSize(1);
        assertThat(hits.get(0).id()).isEqualTo("dup");
    }

    @Test
    void parseDataInstant_aceitaSoDataIsoComoInicioDoDiaEmBrasilia() {
        assertThat(DatajudMovimentoParser.parseDataInstant("2026-01-15")).isPresent();
        Instant t = DatajudMovimentoParser.parseDataInstant("2026-01-15").orElseThrow();
        ZoneId br = ZoneId.of("America/Sao_Paulo");
        assertThat(t).isEqualTo(LocalDate.of(2026, 1, 15).atStartOfDay(br).toInstant());
    }

    @Test
    void parseDataInstant_aceitaDataBr() {
        assertThat(DatajudMovimentoParser.parseDataInstant("15/01/2026")).isPresent();
        assertThat(DatajudMovimentoParser.parseDataInstant("15/01/2026 14:30")).isPresent();
    }

    @Test
    void parseDataInstant_aceitaIsoComEspaco_Datajud() {
        assertThat(DatajudMovimentoParser.parseDataInstant("2026-03-12 12:56:00")).isPresent();
        assertThat(DatajudMovimentoParser.parseDataInstant("2026-03-12 12:56")).isPresent();
    }

    @Test
    void filterByInterval_incluiMovimentoIsoEspacoQuandoIntervaloCobreMarco2026() {
        ZoneId br = ZoneId.of("America/Sao_Paulo");
        Instant inicio = LocalDate.of(2025, 4, 1).atStartOfDay(br).toInstant();
        Instant fim = LocalDate.of(2026, 12, 31).atTime(23, 59, 59).atZone(br).toInstant();
        List<DatajudMovimentoParser.ParsedMovimento> movs = List.of(
                new DatajudMovimentoParser.ParsedMovimento("2026-03-12 12:56:00", "Expedição", "1"));
        List<DatajudMovimentoParser.ParsedMovimento> out =
                DatajudMovimentoParser.filterByInterval(movs, inicio, fim);
        assertThat(out).hasSize(1);
    }

    @Test
    void filterByInterval_incluiMovimentoComApenasDataIsoQuandoIntervaloEmBr() {
        ZoneId br = ZoneId.of("America/Sao_Paulo");
        Instant inicio = LocalDate.of(2026, 1, 1).atStartOfDay(br).toInstant();
        Instant fim = LocalDate.of(2026, 12, 31).atTime(23, 59, 59).atZone(br).toInstant();

        List<DatajudMovimentoParser.ParsedMovimento> movs = List.of(
                new DatajudMovimentoParser.ParsedMovimento("2026-02-10", "só data ISO", "1")
        );
        List<DatajudMovimentoParser.ParsedMovimento> out =
                DatajudMovimentoParser.filterByInterval(movs, inicio, fim);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).descricao()).isEqualTo("só data ISO");
    }

    @Test
    void extractGrau_e_compareGrauLabel() {
        assertThat(DatajudMovimentoParser.extractGrau(Map.of("grau", "G2"))).isEqualTo("G2");
        assertThat(DatajudMovimentoParser.extractGrau(new HashMap<>())).isEqualTo("desconhecido");
        assertThat(DatajudMovimentoParser.compareGrauLabel("G1", "G2")).isNegative();
        assertThat(DatajudMovimentoParser.compareGrauLabel("G2", "G1")).isPositive();
    }
}
