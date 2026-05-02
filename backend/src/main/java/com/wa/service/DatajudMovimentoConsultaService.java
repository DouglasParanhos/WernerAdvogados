package com.wa.service;

import com.wa.client.DatajudClient;
import com.wa.client.DatajudMovimentoParser;
import com.wa.client.DatajudQueryBuilder;
import com.wa.config.DatajudProperties;
import com.wa.dto.DatajudGrauBlocoDTO;
import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import com.wa.dto.DatajudMovimentoItemDTO;
import com.wa.dto.DatajudProcessoMovimentoDTO;
import com.wa.repository.ProcessRepository;
import com.wa.util.NpuNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatajudMovimentoConsultaService {

    private static final ZoneId ZONA_BR = ZoneId.of("America/Sao_Paulo");

    private final ProcessRepository processRepository;
    private final DatajudClient datajudClient;
    private final DatajudProperties datajudProperties;

    private record UmResult(
            DatajudProcessoMovimentoDTO dto,
            int todosCount,
            int filtradosCount,
            int unparsedCount,
            String firstTodoRawData
    ) {
    }

    public DatajudMovimentoConsultaResponseDTO consultarMovimentosDesde(LocalDate dataInicio) {
        if (datajudProperties.getKey() == null || datajudProperties.getKey().isBlank()) {
            throw new IllegalStateException("DATAJUD_API_KEY não configurada no servidor.");
        }

        LocalDate hoje = LocalDate.now(ZONA_BR);
        if (dataInicio.isAfter(hoje)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data atual.");
        }

        ZonedDateTime inicioZdt = dataInicio.atStartOfDay(ZONA_BR);
        Instant inicioInstant = inicioZdt.toInstant();
        Instant fimInstant = ZonedDateTime.now(ZONA_BR).toInstant();

        List<Object[]> rawPairs = processRepository.findIdAndNumeroTjrjAtivos();
        Map<String, Long> idPorNpu = new LinkedHashMap<>();
        for (Object[] row : rawPairs) {
            if (row == null || row.length < 2) {
                continue;
            }
            Object idObj = row[0];
            Object numeroObj = row[1];
            if (!(numeroObj instanceof String numero) || numero.isBlank()) {
                continue;
            }
            String npu = NpuNormalizer.normalizeNpu(numero.trim());
            Long id = (idObj instanceof Number n) ? n.longValue() : null;
            idPorNpu.putIfAbsent(npu, id);
        }
        Set<String> unicos = idPorNpu.keySet();

        List<DatajudProcessoMovimentoDTO> resultados = new ArrayList<>();
        int encontrados = 0;
        int naoEncontrados = 0;
        int erros = 0;

        for (String numero : unicos) {
            UmResult ur = consultarUm(numero, inicioInstant, fimInstant);
            DatajudProcessoMovimentoDTO linha = ur.dto();
            linha.setProcessoId(idPorNpu.get(numero));
            boolean exibirNaListagem =
                    "encontrado".equals(linha.getStatus()) && temMovimentosEmAlgumGrau(linha);
            if (exibirNaListagem) {
                resultados.add(linha);
            }
            switch (linha.getStatus()) {
                case "encontrado" -> encontrados++;
                case "nao_encontrado" -> naoEncontrados++;
                case "erro" -> erros++;
                default -> {
                }
            }
        }

        DatajudMovimentoConsultaResponseDTO.LocalIntervaloDTO intervalo =
                DatajudMovimentoConsultaResponseDTO.LocalIntervaloDTO.builder()
                        .dataInicio(dataInicio.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .ate(ZonedDateTime.now(ZONA_BR).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .zona(ZONA_BR.getId())
                        .build();

        return DatajudMovimentoConsultaResponseDTO.builder()
                .intervalo(intervalo)
                .totalConsultados(unicos.size())
                .totalEncontrados(encontrados)
                .totalNaoEncontrados(naoEncontrados)
                .totalErros(erros)
                .resultados(resultados)
                .build();
    }

    private static boolean temMovimentosEmAlgumGrau(DatajudProcessoMovimentoDTO dto) {
        if (dto.getGraus() == null || dto.getGraus().isEmpty()) {
            return false;
        }
        return dto.getGraus().stream().anyMatch(g -> g.getMovimentos() != null && !g.getMovimentos().isEmpty());
    }

    private UmResult consultarUm(String numeroNormalizado, Instant inicio, Instant fim) {
        int maxMov = Math.max(1, datajudProperties.getMaxMovimentosPorGrau());
        try {
            Map<String, Object> payload = DatajudQueryBuilder.buildSearchByNumeroProcesso(
                    numeroNormalizado,
                    datajudProperties.getMaxHits(),
                    datajudProperties.getMaxHitsCeiling());
            Map<String, Object> response = datajudClient.search(payload);
            List<DatajudMovimentoParser.DocumentHit> hits = DatajudMovimentoParser.parseDocumentHits(response);
            if (hits.isEmpty()) {
                DatajudProcessoMovimentoDTO dto = DatajudProcessoMovimentoDTO.builder()
                        .numeroProcesso(numeroNormalizado)
                        .status("nao_encontrado")
                        .graus(List.of())
                        .build();
                return new UmResult(dto, 0, 0, 0, "");
            }

            Map<String, List<DatajudMovimentoParser.ParsedMovimento>> porGrau = new LinkedHashMap<>();
            for (DatajudMovimentoParser.DocumentHit hit : hits) {
                String grau = DatajudMovimentoParser.extractGrau(hit.source());
                List<DatajudMovimentoParser.ParsedMovimento> movs =
                        DatajudMovimentoParser.parseAndamentos(hit.source());
                porGrau.merge(grau, new ArrayList<>(movs), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
            }

            for (List<DatajudMovimentoParser.ParsedMovimento> list : porGrau.values()) {
                list.sort(Comparator.comparing(DatajudMovimentoParser.ParsedMovimento::data,
                        Comparator.nullsLast(String::compareTo)).reversed());
            }

            List<String> grauKeys = new ArrayList<>(porGrau.keySet());
            grauKeys.sort(DatajudMovimentoParser::compareGrauLabel);

            int todosCount = 0;
            int filtradosCount = 0;
            int unparsedTotal = 0;
            String firstTodoRawData = "";

            List<DatajudGrauBlocoDTO> blocos = new ArrayList<>();
            for (String grau : grauKeys) {
                List<DatajudMovimentoParser.ParsedMovimento> todos = porGrau.get(grau);
                todosCount += todos.size();
                for (DatajudMovimentoParser.ParsedMovimento m : todos) {
                    if (DatajudMovimentoParser.parseDataInstant(m.data()).isEmpty()) {
                        unparsedTotal++;
                    }
                }
                if (firstTodoRawData.isEmpty() && !todos.isEmpty()) {
                    firstTodoRawData = todos.get(0).data();
                }

                List<DatajudMovimentoParser.ParsedMovimento> filtrados =
                        DatajudMovimentoParser.filterByInterval(todos, inicio, fim);
                filtradosCount += filtrados.size();

                List<DatajudMovimentoItemDTO> itens = filtrados.stream()
                        .limit(maxMov)
                        .map(m -> DatajudMovimentoItemDTO.builder()
                                .data(m.data())
                                .descricao(m.descricao())
                                .codigo(m.codigo())
                                .build())
                        .toList();

                if (itens.isEmpty()) {
                    continue;
                }

                Map<String, Object> sample = hits.stream()
                        .filter(h -> DatajudMovimentoParser.extractGrau(h.source()).equals(grau))
                        .findFirst()
                        .map(DatajudMovimentoParser.DocumentHit::source)
                        .orElse(Map.of());

                blocos.add(DatajudGrauBlocoDTO.builder()
                        .grau(grau)
                        .movimentos(itens)
                        .classeProcessual(DatajudMovimentoParser.extractClasseProcessual(sample))
                        .orgaoJulgador(DatajudMovimentoParser.extractOrgaoJulgador(sample))
                        .build());
            }

            DatajudProcessoMovimentoDTO dto = DatajudProcessoMovimentoDTO.builder()
                    .numeroProcesso(numeroNormalizado)
                    .status("encontrado")
                    .graus(blocos)
                    .build();
            return new UmResult(dto, todosCount, filtradosCount, unparsedTotal, firstTodoRawData);
        } catch (Exception e) {
            log.warn("Erro DataJud para {}: {}", numeroNormalizado, e.getMessage());
            DatajudProcessoMovimentoDTO dto = DatajudProcessoMovimentoDTO.builder()
                    .numeroProcesso(numeroNormalizado)
                    .status("erro")
                    .erro(e.getMessage())
                    .graus(List.of())
                    .build();
            return new UmResult(dto, 0, 0, 0, "");
        }
    }
}
