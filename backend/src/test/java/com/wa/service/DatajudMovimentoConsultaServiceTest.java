package com.wa.service;

import com.wa.client.DatajudClient;
import com.wa.client.DatajudQueryBuilder;
import com.wa.config.DatajudProperties;
import com.wa.dto.DatajudMovimentoConsultaResponseDTO;
import com.wa.dto.DatajudProcessoMovimentoDTO;
import com.wa.model.Process;
import com.wa.repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatajudMovimentoConsultaServiceTest {

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private DatajudClient datajudClient;

    @Mock
    private DatajudProperties datajudProperties;

    @InjectMocks
    private DatajudMovimentoConsultaService service;

    @BeforeEach
    void setKey() {
        when(datajudProperties.getKey()).thenReturn("test-key");
        lenient().when(datajudProperties.getMaxHits()).thenReturn(10);
        lenient().when(datajudProperties.getMaxHitsCeiling()).thenReturn(50);
        lenient().when(datajudProperties.getMaxMovimentosPorGrau()).thenReturn(5);
        lenient().when(datajudProperties.getMovimentosBatchParallelism()).thenReturn(4);
    }

    @Test
    void consultar_semChave_lanca() {
        when(datajudProperties.getKey()).thenReturn(" ");
        assertThatThrownBy(() -> service.consultarMovimentosDesde(LocalDate.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DATAJUD_API_KEY");
    }

    @Test
    void consultar_dataFutura_lanca() {
        assertThatThrownBy(() -> service.consultarMovimentosDesde(LocalDate.now().plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consultar_encontrado_filtraMovimentos_eExpoeProcessoId() {
        when(processRepository.findIdAndNumeroTjrjAtivos()).thenReturn(List.<Object[]>of(
                new Object[]{1L, "0059197-75.2023.8.19.0000"}
        ));

        Map<String, Object> source = new HashMap<>();
        source.put("grau", "G1");
        source.put(
                "movimentos",
                List.of(
                        Map.of("dataHora", "1900-01-01T12:00:00Z", "nome", "velho", "codigo", "1"),
                        Map.of(
                                "dataHora",
                                LocalDate.now().minusDays(1).toString() + "T15:00:00Z",
                                "nome",
                                "recente",
                                "codigo",
                                "2")));

        Map<String, Object> response =
                Map.of("hits", Map.of("hits", List.of(Map.of("_id", "a", "_source", source))));

        when(datajudClient.search(any())).thenAnswer(inv -> {
            Map<String, Object> payload = inv.getArgument(0);
            assertThat(payload)
                    .isEqualTo(DatajudQueryBuilder.buildSearchByNumeroProcesso("0059197-75.2023.8.19.0000", 10, 50));
            return response;
        });

        DatajudMovimentoConsultaResponseDTO dto =
                service.consultarMovimentosDesde(LocalDate.now().minusDays(7));

        assertThat(dto.getTotalConsultados()).isEqualTo(1);
        assertThat(dto.getTotalEncontrados()).isEqualTo(1);
        assertThat(dto.getResultados()).hasSize(1);
        assertThat(dto.getResultados().get(0).getProcessoId()).isEqualTo(1L);
        assertThat(dto.getResultados().get(0).getGraus()).hasSize(1);
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos()).hasSize(1);
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos().get(0).getDescricao())
                .isEqualTo("recente");
    }

    @Test
    void consultar_encontrado_limitaCincoUltimosMovimentos() {
        when(processRepository.findIdAndNumeroTjrjAtivos()).thenReturn(List.<Object[]>of(
                new Object[]{42L, "0059197-75.2023.8.19.0000"}
        ));

        LocalDate hoje = LocalDate.now();
        Map<String, Object> source = new HashMap<>();
        source.put("grau", "G1");
        source.put(
                "movimentos",
                List.of(
                        Map.of("dataHora", hoje.minusDays(7).toString() + "T08:00:00Z", "nome", "m1", "codigo", "1"),
                        Map.of("dataHora", hoje.minusDays(6).toString() + "T08:00:00Z", "nome", "m2", "codigo", "2"),
                        Map.of("dataHora", hoje.minusDays(5).toString() + "T08:00:00Z", "nome", "m3", "codigo", "3"),
                        Map.of("dataHora", hoje.minusDays(4).toString() + "T08:00:00Z", "nome", "m4", "codigo", "4"),
                        Map.of("dataHora", hoje.minusDays(3).toString() + "T08:00:00Z", "nome", "m5", "codigo", "5"),
                        Map.of("dataHora", hoje.minusDays(2).toString() + "T08:00:00Z", "nome", "m6", "codigo", "6"),
                        Map.of("dataHora", hoje.minusDays(1).toString() + "T08:00:00Z", "nome", "m7", "codigo", "7")));

        Map<String, Object> response =
                Map.of("hits", Map.of("hits", List.of(Map.of("_id", "g1", "_source", source))));

        when(datajudClient.search(any())).thenReturn(response);

        DatajudMovimentoConsultaResponseDTO dto =
                service.consultarMovimentosDesde(LocalDate.now().minusDays(10));

        assertThat(dto.getResultados()).hasSize(1);
        assertThat(dto.getResultados().get(0).getProcessoId()).isEqualTo(42L);
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos()).hasSize(5);
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos().get(0).getDescricao())
                .isEqualTo("m7");
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos().get(4).getDescricao())
                .isEqualTo("m3");
    }

    @Test
    void consultar_encontradoSemMovimentoNoPeriodo_naoApareceNaListagem() {
        when(processRepository.findIdAndNumeroTjrjAtivos()).thenReturn(List.<Object[]>of(
                new Object[]{7L, "0059197-75.2023.8.19.0000"}
        ));

        Map<String, Object> source = new HashMap<>();
        source.put("grau", "G1");
        source.put("movimentos", List.of(
                Map.of("dataHora", "1900-01-01T12:00:00Z", "nome", "muito antigo", "codigo", "1")
        ));

        Map<String, Object> response =
                Map.of("hits", Map.of("hits", List.of(Map.of("_id", "x", "_source", source))));

        when(datajudClient.search(any())).thenReturn(response);

        DatajudMovimentoConsultaResponseDTO dto =
                service.consultarMovimentosDesde(LocalDate.now().minusDays(7));

        assertThat(dto.getTotalConsultados()).isEqualTo(1);
        assertThat(dto.getTotalEncontrados()).isEqualTo(1);
        assertThat(dto.getResultados()).isEmpty();
    }

    @Test
    void consultar_doisGraus_retornaDoisBlocosOrdenados() {
        when(processRepository.findIdAndNumeroTjrjAtivos()).thenReturn(List.<Object[]>of(
                new Object[]{99L, "0059197-75.2023.8.19.0000"}
        ));

        LocalDate hoje = LocalDate.now();
        String d = hoje.minusDays(1).toString() + "T12:00:00Z";

        Map<String, Object> s1 = new HashMap<>();
        s1.put("grau", "G1");
        s1.put("movimentos", List.of(Map.of("dataHora", d, "nome", "primeiro", "codigo", "1")));

        Map<String, Object> s2 = new HashMap<>();
        s2.put("grau", "G2");
        s2.put("movimentos", List.of(Map.of("dataHora", d, "nome", "segundo", "codigo", "2")));

        Map<String, Object> response = Map.of(
                "hits",
                Map.of(
                        "hits",
                        List.of(
                                Map.of("_id", "id-g2", "_source", s2),
                                Map.of("_id", "id-g1", "_source", s1))));

        when(datajudClient.search(any())).thenReturn(response);

        DatajudMovimentoConsultaResponseDTO dto =
                service.consultarMovimentosDesde(LocalDate.now().minusDays(5));

        assertThat(dto.getResultados()).hasSize(1);
        assertThat(dto.getResultados().get(0).getGraus()).hasSize(2);
        assertThat(dto.getResultados().get(0).getGraus().get(0).getGrau()).isEqualTo("G1");
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos().get(0).getDescricao())
                .isEqualTo("primeiro");
        assertThat(dto.getResultados().get(0).getGraus().get(1).getGrau()).isEqualTo("G2");
        assertThat(dto.getResultados().get(0).getGraus().get(1).getMovimentos().get(0).getDescricao())
                .isEqualTo("segundo");
    }

    @Test
    void consultar_limitaCincoPorGrau_independentemente() {
        when(datajudProperties.getMaxMovimentosPorGrau()).thenReturn(5);
        when(processRepository.findIdAndNumeroTjrjAtivos()).thenReturn(List.<Object[]>of(
                new Object[]{1L, "0059197-75.2023.8.19.0000"}
        ));

        LocalDate hoje = LocalDate.now();
        Map<String, Object> g1 = new HashMap<>();
        g1.put("grau", "G1");
        List<Map<String, Object>> m1 = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            m1.add(Map.of(
                    "dataHora",
                    hoje.minusDays(i + 1).toString() + "T10:00:00Z",
                    "nome",
                    "g1-" + i,
                    "codigo",
                    String.valueOf(i)));
        }
        g1.put("movimentos", m1);

        Map<String, Object> g2 = new HashMap<>();
        g2.put("grau", "G2");
        List<Map<String, Object>> m2 = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            m2.add(Map.of(
                    "dataHora",
                    hoje.minusDays(i + 1).toString() + "T11:00:00Z",
                    "nome",
                    "g2-" + i,
                    "codigo",
                    String.valueOf(i + 100)));
        }
        g2.put("movimentos", m2);

        Map<String, Object> response = Map.of(
                "hits",
                Map.of(
                        "hits",
                        List.of(
                                Map.of("_id", "h1", "_source", g1),
                                Map.of("_id", "h2", "_source", g2))));

        when(datajudClient.search(any())).thenReturn(response);

        DatajudMovimentoConsultaResponseDTO dto =
                service.consultarMovimentosDesde(LocalDate.now().minusDays(15));

        assertThat(dto.getResultados().get(0).getGraus()).hasSize(2);
        assertThat(dto.getResultados().get(0).getGraus().get(0).getMovimentos()).hasSize(5);
        assertThat(dto.getResultados().get(0).getGraus().get(1).getMovimentos()).hasSize(5);
    }

    @Test
    void consultar_naoEncontrado_naoApareceNaListagemMasContaNoTotal() {
        when(processRepository.findIdAndNumeroTjrjAtivos()).thenReturn(List.<Object[]>of(
                new Object[]{9L, "0059197-75.2023.8.19.0000"}
        ));
        when(datajudClient.search(any())).thenReturn(Map.of("hits", Map.of("hits", List.of())));

        DatajudMovimentoConsultaResponseDTO dto =
                service.consultarMovimentosDesde(LocalDate.now().minusDays(1));

        assertThat(dto.getTotalNaoEncontrados()).isEqualTo(1);
        assertThat(dto.getResultados()).isEmpty();
    }

    @Test
    void consultarMovimentosProcesso_processoInexistente_lancaEntityNotFound() {
        when(processRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarMovimentosProcesso(999L, LocalDate.now().minusDays(1)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Processo");
    }

    @Test
    void consultarMovimentosProcesso_semSegmentoTjrj_lanca() {
        Process p = new Process();
        p.setId(1L);
        p.setNumero("0000000-00.0000.0.00.0000");
        when(processRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> service.consultarMovimentosProcesso(1L, LocalDate.now().minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(".8.19.");
    }

    @Test
    void consultarMovimentosProcesso_encontrado_preencheProcessoId() {
        Process p = new Process();
        p.setId(77L);
        p.setNumero("0059197-75.2023.8.19.0000");
        when(processRepository.findById(77L)).thenReturn(Optional.of(p));

        LocalDate hoje = LocalDate.now();
        Map<String, Object> source = new HashMap<>();
        source.put("grau", "G1");
        source.put(
                "movimentos",
                List.of(
                        Map.of(
                                "dataHora",
                                hoje.minusDays(1).toString() + "T15:00:00Z",
                                "nome",
                                "recente",
                                "codigo",
                                "2")));

        when(datajudClient.search(any())).thenReturn(
                Map.of("hits", Map.of("hits", List.of(Map.of("_id", "a", "_source", source)))));

        DatajudProcessoMovimentoDTO one = service.consultarMovimentosProcesso(77L, LocalDate.now().minusDays(7));

        assertThat(one.getProcessoId()).isEqualTo(77L);
        assertThat(one.getStatus()).isEqualTo("encontrado");
        assertThat(one.getGraus()).hasSize(1);
        assertThat(one.getGraus().get(0).getMovimentos()).hasSize(1);
        assertThat(one.getGraus().get(0).getMovimentos().get(0).getDescricao()).isEqualTo("recente");
    }
}
