package com.wa.service;

import com.wa.client.BcbApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EconomicIndexService {
    
    private final BcbApiClient bcbApiClient;
    
    private static final LocalDate DATA_CORTE_IPCA = LocalDate.of(2021, 11, 30);
    private static final LocalDate DATA_INICIO_SELIC = LocalDate.of(2021, 12, 9);
    private static final DateTimeFormatter FORMATTER_MES_ANO = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final DateTimeFormatter FORMATTER_DATA_BCB = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Obtém IPCA-E acumulado desde uma data até 30/11/2021
     * Usando valor fixo conforme especificação: 2,88680560
     */
    public BigDecimal getIpcaEAcumulado(LocalDate dataInicio, LocalDate dataFim) {
        // Valor fixo conforme especificação do usuário
        return new BigDecimal("2.88680560").setScale(8, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtém Taxa SELIC acumulada desde 09/12/2021 até hoje
     * A SELIC é calculada diariamente usando capitalização composta
     * Baseado na metodologia da Calculadora do Cidadão do BCB
     */
    public BigDecimal getSelicAcumulada(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isBefore(DATA_INICIO_SELIC)) {
            dataInicio = DATA_INICIO_SELIC;
        }
        
        if (dataInicio.isAfter(dataFim)) {
            return BigDecimal.ONE;
        }
        
        // Buscar dados diários da SELIC do BCB
        Map<String, BigDecimal> selicData = buscarSelicPeriodo(dataInicio, dataFim);
        
        log.info("SELIC: Buscando dados de {} a {}, encontrados {} registros", dataInicio, dataFim, selicData.size());
        
        if (selicData.isEmpty()) {
            log.warn("Nenhum dado SELIC encontrado para o período {} a {}", dataInicio, dataFim);
            return BigDecimal.ONE;
        }
        
        // Verificar primeira data disponível
        String primeiraDataDisponivel = selicData.keySet().stream().min(String::compareTo).orElse(null);
        if (primeiraDataDisponivel != null) {
            log.info("SELIC: Primeira data disponível: {}", primeiraDataDisponivel);
        }
        
        // Calcular fator acumulado dia a dia
        // A SELIC é capitalizada diariamente usando a fórmula da Calculadora do Cidadão do BCB
        // Fórmula: fator_diario = (1 + taxa_anual/100)^(1/252)
        BigDecimal fatorAcumulado = BigDecimal.ONE;
        LocalDate dataAtual = dataInicio;
        BigDecimal selicAnterior = null;
        
        // Se a data inicial for anterior à primeira data disponível, usar a primeira taxa disponível
        if (primeiraDataDisponivel != null && dataInicio.isBefore(LocalDate.parse(primeiraDataDisponivel, FORMATTER_DATA_BCB))) {
            selicAnterior = selicData.get(primeiraDataDisponivel);
            log.info("SELIC: Usando taxa inicial de {}: {}", primeiraDataDisponivel, selicAnterior);
        }
        
        // Contar apenas dias úteis (excluir sábados e domingos)
        int diasUteis = 0;
        while (!dataAtual.isAfter(dataFim)) {
            // Verificar se é dia útil (segunda a sexta)
            java.time.DayOfWeek diaSemana = dataAtual.getDayOfWeek();
            if (diaSemana == java.time.DayOfWeek.SATURDAY || diaSemana == java.time.DayOfWeek.SUNDAY) {
                dataAtual = dataAtual.plusDays(1);
                continue;
            }
            
            String dataStr = dataAtual.format(FORMATTER_DATA_BCB);
            BigDecimal selicDia = selicData.get(dataStr);
            
            // Se não houver valor para este dia, usar o último valor conhecido
            if (selicDia == null || selicDia.compareTo(BigDecimal.ZERO) <= 0) {
                if (selicAnterior != null) {
                    selicDia = selicAnterior;
                } else {
                    // Se ainda não temos taxa anterior e não há dados, pular este dia
                    dataAtual = dataAtual.plusDays(1);
                    continue;
                }
            } else {
                selicAnterior = selicDia;
            }
            
            // Calcular fator diário usando capitalização composta correta
            // Fórmula da Calculadora do Cidadão: (1 + taxa_anual/100)^(1/252)
            BigDecimal taxaAnualDecimal = selicDia.divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
            double taxaAnualDouble = taxaAnualDecimal.doubleValue();
            double fatorDiaDouble = Math.pow(1.0 + taxaAnualDouble, 1.0 / 252.0);
            BigDecimal fatorDia = BigDecimal.valueOf(fatorDiaDouble).setScale(15, RoundingMode.HALF_UP);
            
            fatorAcumulado = fatorAcumulado.multiply(fatorDia);
            diasUteis++;
            
            dataAtual = dataAtual.plusDays(1);
        }
        
        log.info("SELIC: Processados {} dias úteis de {} a {}", diasUteis, dataInicio, dataFim);
        
        log.info("SELIC: Fator acumulado calculado: {}", fatorAcumulado);
        return fatorAcumulado.setScale(8, RoundingMode.HALF_UP);
    }
    
    /**
     * Busca IPCA-E para um período específico
     * A série 433 do BCB retorna valores mensais (último dia de cada mês)
     */
    private Map<String, BigDecimal> buscarIpcaEPeriodo(LocalDate inicio, LocalDate fim) {
        String dataInicioStr = inicio.format(FORMATTER_DATA_BCB);
        String dataFimStr = fim.format(FORMATTER_DATA_BCB);
        
        Map<String, BigDecimal> resultado = new HashMap<>();
        Map<String, BigDecimal> dadosBcb = bcbApiClient.getIpcaEHistorico(dataInicioStr, dataFimStr);
        
        log.info("IPCA-E: Dados retornados do BCB: {} registros", dadosBcb.size());
        
        // Agrupar por mês/ano - usar o último valor do mês (último dia do mês)
        YearMonth mesAtual = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);
        
        while (!mesAtual.isAfter(mesFim)) {
            String mesAno = mesAtual.format(FORMATTER_MES_ANO);
            
            // Buscar o valor do último dia do mês (série 433 retorna valores mensais no último dia)
            LocalDate ultimoDiaMes = mesAtual.atEndOfMonth();
            String dataUltimoDia = ultimoDiaMes.format(FORMATTER_DATA_BCB);
            
            BigDecimal valorMes = dadosBcb.get(dataUltimoDia);
            
            // Se não encontrar no último dia, buscar qualquer valor do mês
            if (valorMes == null) {
                for (Map.Entry<String, BigDecimal> entry : dadosBcb.entrySet()) {
                    String dataEntry = entry.getKey();
                    if (dataEntry.endsWith("/" + mesAno.split("/")[1]) && 
                        dataEntry.startsWith(mesAno.split("/")[0] + "/")) {
                        valorMes = entry.getValue();
                        break; // Usar o primeiro valor encontrado do mês
                    }
                }
            }
            
            if (valorMes != null && valorMes.compareTo(BigDecimal.ZERO) > 0) {
                resultado.put(mesAno, valorMes);
                log.debug("IPCA-E {}: {}", mesAno, valorMes);
            } else {
                log.warn("IPCA-E: Valor não encontrado para {}", mesAno);
            }
            
            mesAtual = mesAtual.plusMonths(1);
        }
        
        log.info("IPCA-E: {} meses processados de {} a {}", resultado.size(), 
                YearMonth.from(inicio).format(FORMATTER_MES_ANO),
                YearMonth.from(fim).format(FORMATTER_MES_ANO));
        
        return resultado;
    }
    
    /**
     * Busca SELIC para um período específico (retorna dados diários)
     * A série 11 do BCB retorna taxa SELIC diária (em % ao ano)
     */
    private Map<String, BigDecimal> buscarSelicPeriodo(LocalDate inicio, LocalDate fim) {
        // A série 11 pode não ter dados desde 09/12/2021, então vamos buscar desde uma data anterior
        // Se não houver dados desde a data inicial, vamos buscar desde a primeira data disponível
        LocalDate dataBuscaInicio = inicio;
        if (inicio.isBefore(LocalDate.of(2022, 1, 1))) {
            // Tentar buscar desde 01/01/2022 se a data inicial for anterior
            dataBuscaInicio = LocalDate.of(2022, 1, 1);
        }
        
        String dataInicioStr = dataBuscaInicio.format(FORMATTER_DATA_BCB);
        String dataFimStr = fim.format(FORMATTER_DATA_BCB);
        
        // Buscar dados diários do BCB diretamente
        Map<String, BigDecimal> dadosBcb = bcbApiClient.getSelicHistorico(dataInicioStr, dataFimStr);
        
        log.info("SELIC: Dados retornados do BCB: {} registros de {} a {}", dadosBcb.size(), dataInicioStr, dataFimStr);
        if (!dadosBcb.isEmpty()) {
            String primeiraData = dadosBcb.keySet().stream().min(String::compareTo).orElse("N/A");
            String ultimaData = dadosBcb.keySet().stream().max(String::compareTo).orElse("N/A");
            log.info("SELIC: Primeira data disponível: {}, Última data: {}", primeiraData, ultimaData);
            
            // Se a primeira data disponível é posterior à data inicial solicitada,
            // usar a primeira taxa disponível para o período anterior
            if (inicio.isBefore(LocalDate.parse(primeiraData, FORMATTER_DATA_BCB))) {
                BigDecimal primeiraTaxa = dadosBcb.get(primeiraData);
                log.info("SELIC: Usando taxa {} para período anterior a {}", primeiraTaxa, primeiraData);
                
                // Adicionar entradas para os dias anteriores usando a primeira taxa disponível
                LocalDate dataPreenche = inicio;
                LocalDate primeiraDataDate = LocalDate.parse(primeiraData, FORMATTER_DATA_BCB);
                while (dataPreenche.isBefore(primeiraDataDate)) {
                    String dataStr = dataPreenche.format(FORMATTER_DATA_BCB);
                    dadosBcb.put(dataStr, primeiraTaxa);
                    dataPreenche = dataPreenche.plusDays(1);
                }
            }
        }
        
        // Retornar dados diários (não agrupar por mês)
        return dadosBcb;
    }
    
    /**
     * Obtém taxa de juros de poupança (conforme Tema 905/STJ)
     * A partir de 04/05/2012: juros da caderneta de poupança (0,5% a.m.)
     */
    public BigDecimal getTaxaJurosPoupanca(LocalDate data) {
        // Taxa de poupança: 0.5% ao mês (conforme observação do cálculo oficial)
        // No período de 30/06/2009 a 03/05/2012 também é 0,5% a.m.
        return BigDecimal.valueOf(0.5);
    }
}

