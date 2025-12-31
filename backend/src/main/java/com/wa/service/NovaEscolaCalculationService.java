package com.wa.service;

import com.wa.dto.ParcelaCalculadaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NovaEscolaCalculationService {
    
    private final EconomicIndexService economicIndexService;
    
    private static final LocalDate DATA_CORTE_IPCA = LocalDate.of(2021, 11, 30);
    private static final LocalDate DATA_INICIO_SELIC = LocalDate.of(2021, 12, 9);
    private static final LocalDate DATA_CITACAO = LocalDate.of(2007, 2, 6); // 06/02/2007 conforme especificação
    private static final LocalDate DATA_FIM_JUROS_6_PORCENTO = LocalDate.of(2009, 6, 30); // Até junho/2009: 6% a.a.
    private static final LocalDate DATA_INICIO_JUROS_POUPANCA = LocalDate.of(2009, 7, 1); // A partir de julho/2009: poupança
    private static final LocalDate DATA_FIM_JUROS_POUPANCA = LocalDate.of(2021, 12, 8); // Até 08/12/2021
    private static final DateTimeFormatter FORMATTER_MES_ANO = DateTimeFormatter.ofPattern("MM/yyyy");
    
    /**
     * Calcula todas as parcelas do NOVAESCOLA usando os fatores fornecidos
     */
    public List<ParcelaCalculadaDTO> calcularParcelas(BigDecimal valorBase, BigDecimal ipcaEFactor, BigDecimal selicFactor) {
        List<ParcelaCalculadaDTO> parcelas = new ArrayList<>();
        
        // Parcelas mensais de jan/2003 a dez/2003
        for (int mes = 1; mes <= 12; mes++) {
            YearMonth mesAno = YearMonth.of(2003, mes);
            LocalDate dataVencimento = mesAno.atEndOfMonth();
            
            ParcelaCalculadaDTO parcela = calcularParcela(
                    mesAno.format(FORMATTER_MES_ANO),
                    dataVencimento,
                    valorBase,
                    ipcaEFactor,
                    selicFactor
            );
            
            parcelas.add(parcela);
        }
        
        // 13º Salário / 2003
        LocalDate dataVencimento13 = LocalDate.of(2003, 12, 20); // Data típica de pagamento do 13º
        
        ParcelaCalculadaDTO parcela13 = calcularParcela(
                "13º Salário / 2003",
                dataVencimento13,
                valorBase,
                ipcaEFactor,
                selicFactor
        );
        
        parcelas.add(parcela13);
        
        return parcelas;
    }
    
    /**
     * Calcula uma parcela individual usando os fatores fornecidos
     */
    private ParcelaCalculadaDTO calcularParcela(String mesAno, LocalDate dataVencimento, BigDecimal valorBase, 
                                                 BigDecimal ipcaEFactor, BigDecimal selicFactor) {
        ParcelaCalculadaDTO parcela = new ParcelaCalculadaDTO();
        parcela.setMesAno(mesAno);
        parcela.setDataVencimento(dataVencimento);
        parcela.setValorBase(valorBase);
        parcela.setDataInicialJuros(DATA_CITACAO);
        
        // 1. Correção Monetária até 30/11/2021 (IPCA-E)
        // O fator IPCA-E fornecido é acumulado desde 02/2003 até 30/11/2021
        // Para cada parcela, calculamos o fator desde sua data de vencimento até 30/11/2021
        // O fator fornecido é usado como referência/validação
        BigDecimal fatorIpcaParcela = economicIndexService.getIpcaEAcumulado(
                dataVencimento, 
                DATA_CORTE_IPCA
        );
        
        log.info("Parcela {}: Fator IPCA-E calculado desde {} até {}: {}", 
                mesAno, dataVencimento, DATA_CORTE_IPCA, fatorIpcaParcela);
        
        parcela.setIndiceCorrecaoMonetaria("IPCA-E");
        parcela.setFatorCorrecaoMonetaria(fatorIpcaParcela);
        BigDecimal valorCorrigidoIpca = valorBase.multiply(fatorIpcaParcela).setScale(2, RoundingMode.HALF_UP);
        parcela.setValorCorrigidoAte08122021(valorCorrigidoIpca);
        
        // 2. Atualização pela SELIC a partir de 09/12/2021 usando o fator fornecido
        BigDecimal fatorSelic = selicFactor != null ? selicFactor : BigDecimal.ONE;
        
        log.info("Usando fator SELIC fornecido para parcela {}: {}", mesAno, fatorSelic);
        
        parcela.setFatorSelic(fatorSelic);
        parcela.setValorAtualizadoSelic(
                valorCorrigidoIpca
                        .multiply(fatorSelic)
                        .setScale(2, RoundingMode.HALF_UP)
        );
        
        // 3. Desconto de Previdência (-11%)
        BigDecimal descontoPrevidencia = valorBase
                .multiply(BigDecimal.valueOf(0.11))
                .setScale(2, RoundingMode.HALF_UP);
        parcela.setDescontoPrevidencia(descontoPrevidencia);
        
        // 4. Juros de Mora (calculados sobre o valor corrigido até 30/11/2021) - JUROS SIMPLES
        BigDecimal valorJuros = calcularJurosMoraSimples(
                valorCorrigidoIpca,
                dataVencimento,
                parcela
        );
        
        parcela.setValorJurosMora(valorJuros);
        
        // 5. Valor Total Devido
        parcela.setValorTotalDevido(
                parcela.getValorAtualizadoSelic()
                        .add(valorJuros)
                        .setScale(2, RoundingMode.HALF_UP)
        );
        
        return parcela;
    }
    
    /**
     * Calcula juros de mora SIMPLES conforme Tema 905/STJ
     * Juros simples: sempre calculados sobre o valor base (sem capitalização)
     */
    private BigDecimal calcularJurosMoraSimples(
            BigDecimal valorBaseJuros,
            LocalDate dataVencimento,
            ParcelaCalculadaDTO parcela
    ) {
        // Juros só começam a partir da data da citação (06/02/2007)
        // Se a parcela venceu antes da citação, juros começam na citação
        // Se a parcela venceu depois da citação, juros começam no vencimento
        LocalDate dataInicioJuros = dataVencimento.isAfter(DATA_CITACAO) 
                ? dataVencimento 
                : DATA_CITACAO;
        
        parcela.setDataInicialJuros(dataInicioJuros);
        
        // Período de incidência
        LocalDate dataFimJuros = DATA_FIM_JUROS_POUPANCA; // Até 08/12/2021
        
        // Calcular juros SIMPLES até 08/12/2021
        JurosResultado resultado = calcularJurosSimplesPeriodoCompleto(
                valorBaseJuros,
                dataInicioJuros,
                dataFimJuros
        );
        
        // A partir de 09/12/2021: juros incluídos na SELIC
        parcela.setPeriodoIncidenciaJuros(
                formatarPeriodo(dataInicioJuros, DATA_FIM_JUROS_POUPANCA) + 
                " (juros separados) e a partir de 09/12/2021 (incluídos na SELIC)"
        );
        parcela.setTaxaJurosAplicada(resultado.taxaJurosAplicada);
        parcela.setFatorJuros(resultado.fatorJuros);
        
        return resultado.valorJuros.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula juros de mora conforme Tema 905/STJ (método antigo - compostos)
     * @deprecated Usar calcularJurosMoraSimples
     */
    private BigDecimal calcularJurosMora(
            BigDecimal valorBaseJuros,
            LocalDate dataVencimento,
            ParcelaCalculadaDTO parcela
    ) {
        // Juros só começam a partir da data da citação (06/02/2007)
        // Se a parcela venceu antes da citação, juros começam na citação
        // Se a parcela venceu depois da citação, juros começam no vencimento
        LocalDate dataInicioJuros = dataVencimento.isAfter(DATA_CITACAO) 
                ? dataVencimento 
                : DATA_CITACAO;
        
        parcela.setDataInicialJuros(dataInicioJuros);
        
        // Período de incidência
        LocalDate dataFimJuros;
        
        // Juros são calculados até 30/11/2021 (após isso, estão incluídos na SELIC)
        dataFimJuros = DATA_FIM_JUROS_POUPANCA;
        
        // Calcular juros até 30/11/2021
        JurosResultado resultado = calcularJurosPeriodoCompleto(
                valorBaseJuros,
                dataInicioJuros,
                dataFimJuros
        );
        
        // A partir de 09/12/2021: juros incluídos na SELIC
        // Não adicionar juros separados, pois já estão na SELIC
        
            parcela.setPeriodoIncidenciaJuros(
                    formatarPeriodo(dataInicioJuros, DATA_FIM_JUROS_POUPANCA) + 
                    " (juros separados) e a partir de 09/12/2021 (incluídos na SELIC)"
            );
        parcela.setTaxaJurosAplicada(resultado.taxaJurosAplicada);
        parcela.setFatorJuros(resultado.fatorJuros);
        
        return resultado.valorJuros.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula juros SIMPLES para um período específico (sem capitalização)
     * Juros sempre calculados sobre o valor base original
     */
    private JurosResultado calcularJurosSimplesPeriodoCompleto(
            BigDecimal valorBase,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        JurosResultado resultado = new JurosResultado();
        resultado.valorJuros = BigDecimal.ZERO;
        resultado.fatorJuros = BigDecimal.ONE;
        
        if (dataInicio.isAfter(dataFim)) {
            resultado.taxaJurosAplicada = "N/A";
            return resultado;
        }
        
        StringBuilder taxaDescricao = new StringBuilder();
        BigDecimal jurosTotal = BigDecimal.ZERO;
        YearMonth mesAtual = YearMonth.from(dataInicio);
        YearMonth mesFim = YearMonth.from(dataFim);
        
        // Em juros simples, sempre calculamos sobre o valor base original (sem capitalização)
        BigDecimal valorBaseJuros = valorBase;
        
        while (!mesAtual.isAfter(mesFim)) {
            LocalDate inicioMes = mesAtual.atDay(1);
            LocalDate fimMes = mesAtual.atEndOfMonth();
            
            // Ajustar datas do período
            if (inicioMes.isBefore(dataInicio)) {
                inicioMes = dataInicio;
            }
            if (fimMes.isAfter(dataFim)) {
                fimMes = dataFim;
            }
            
            // Determinar taxa de juros conforme período
            BigDecimal taxaJuros;
            String descricaoTaxa;
            
            // Conforme Tema 905/STJ para servidores públicos:
            // - agosto/2001 a junho/2009: juros 0,5% ao mês
            // - a partir de julho/2009: juros = remuneração oficial da caderneta de poupança (0,5% a.m.)
            
            if (fimMes.isBefore(DATA_INICIO_JUROS_POUPANCA) || fimMes.equals(DATA_FIM_JUROS_6_PORCENTO)) {
                // Período agosto/2001 até 30/06/2009: 0,5% ao mês (conforme Tema 905/STJ)
                taxaJuros = BigDecimal.valueOf(0.5);
                descricaoTaxa = "0,5% a.m. (Tema 905/STJ - período agosto/2001 a junho/2009)";
            } else if (inicioMes.isAfter(DATA_FIM_JUROS_6_PORCENTO) || inicioMes.equals(DATA_INICIO_JUROS_POUPANCA)) {
                // Período a partir de 01/07/2009: remuneração oficial da caderneta de poupança (Tema 905/STJ)
                taxaJuros = BigDecimal.valueOf(0.5);
                descricaoTaxa = "0,5% a.m. (poupança - Tema 905/STJ)";
            } else {
                // Mês de transição (junho/julho 2009) - calcular proporcionalmente
                taxaJuros = BigDecimal.valueOf(0.5);
                descricaoTaxa = "0,5% a.m. (Tema 905/STJ/Poupança)";
            }
            
            if (taxaDescricao.length() == 0) {
                taxaDescricao.append(descricaoTaxa);
            } else if (!taxaDescricao.toString().contains(descricaoTaxa)) {
                taxaDescricao.append("; ").append(descricaoTaxa);
            }
            
            // Calcular juros do mês (JUROS SIMPLES - sempre sobre o valor base original)
            BigDecimal diasNoMes = BigDecimal.valueOf(fimMes.toEpochDay() - inicioMes.toEpochDay() + 1);
            BigDecimal diasMesTotal = BigDecimal.valueOf(mesAtual.lengthOfMonth());
            BigDecimal proporcaoMes = diasNoMes.divide(diasMesTotal, 6, RoundingMode.HALF_UP);
            
            // JUROS SIMPLES: sempre sobre valorBaseJuros (sem capitalização)
            BigDecimal valorJurosMes = valorBaseJuros
                    .multiply(taxaJuros)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                    .multiply(proporcaoMes);
            
            jurosTotal = jurosTotal.add(valorJurosMes);
            
            // NÃO atualizar valor base (juros simples não capitalizam)
            
            mesAtual = mesAtual.plusMonths(1);
        }
        
        resultado.valorJuros = jurosTotal;
        resultado.taxaJurosAplicada = taxaDescricao.toString();
        // Fator de juros simples = 1 + (juros total / valor base)
        resultado.fatorJuros = BigDecimal.ONE.add(
                jurosTotal.divide(valorBase, 6, RoundingMode.HALF_UP)
        );
        
        return resultado;
    }
    
    /**
     * Calcula juros para um período específico com informações completas (COMPOSTOS)
     * @deprecated Usar calcularJurosSimplesPeriodoCompleto
     */
    private JurosResultado calcularJurosPeriodoCompleto(
            BigDecimal valorBase,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        JurosResultado resultado = new JurosResultado();
        resultado.valorJuros = BigDecimal.ZERO;
        resultado.fatorJuros = BigDecimal.ONE;
        
        if (dataInicio.isAfter(dataFim)) {
            resultado.taxaJurosAplicada = "N/A";
            return resultado;
        }
        
        StringBuilder taxaDescricao = new StringBuilder();
        BigDecimal jurosTotal = BigDecimal.ZERO;
        YearMonth mesAtual = YearMonth.from(dataInicio);
        YearMonth mesFim = YearMonth.from(dataFim);
        BigDecimal valorAcumulado = valorBase;
        
        while (!mesAtual.isAfter(mesFim)) {
            LocalDate inicioMes = mesAtual.atDay(1);
            LocalDate fimMes = mesAtual.atEndOfMonth();
            
            // Ajustar datas do período
            if (inicioMes.isBefore(dataInicio)) {
                inicioMes = dataInicio;
            }
            if (fimMes.isAfter(dataFim)) {
                fimMes = dataFim;
            }
            
            // Determinar taxa de juros conforme período
            BigDecimal taxaJuros;
            String descricaoTaxa;
            
            // Conforme Tema 905/STJ para servidores públicos:
            // - até julho/2001: juros 1% ao mês (capitalização simples) - não aplicável (citação em 2007)
            // - agosto/2001 a junho/2009: juros 0,5% ao mês
            // - a partir de julho/2009: juros = remuneração oficial da caderneta de poupança (0,5% a.m.)
            // - EC 113/2021: a partir de 09/12/2021, juros incluídos na SELIC
            
            if (fimMes.isBefore(DATA_INICIO_JUROS_POUPANCA) || fimMes.equals(DATA_FIM_JUROS_6_PORCENTO)) {
                // Período agosto/2001 até 30/06/2009: 0,5% ao mês (conforme Tema 905/STJ)
                taxaJuros = BigDecimal.valueOf(0.5);
                descricaoTaxa = "0,5% a.m. (Tema 905/STJ - período agosto/2001 a junho/2009)";
            } else if (inicioMes.isAfter(DATA_FIM_JUROS_6_PORCENTO) || inicioMes.equals(DATA_INICIO_JUROS_POUPANCA)) {
                // Período a partir de 01/07/2009: remuneração oficial da caderneta de poupança (Tema 905/STJ)
                taxaJuros = BigDecimal.valueOf(0.5);
                descricaoTaxa = "0,5% a.m. (poupança - Tema 905/STJ)";
            } else {
                // Mês de transição (junho/julho 2009) - calcular proporcionalmente
                // Dias até 30/06: 0,5% a.m. (Tema 905), dias a partir de 01/07: poupança
                // Mas como ambas são 0,5%, usar 0,5%
                taxaJuros = BigDecimal.valueOf(0.5);
                descricaoTaxa = "0,5% a.m. (Tema 905/STJ/Poupança)";
            }
            
            if (taxaDescricao.length() == 0) {
                taxaDescricao.append(descricaoTaxa);
            } else if (!taxaDescricao.toString().contains(descricaoTaxa)) {
                taxaDescricao.append("; ").append(descricaoTaxa);
            }
            
            // Calcular juros do mês
            BigDecimal diasNoMes = BigDecimal.valueOf(fimMes.toEpochDay() - inicioMes.toEpochDay() + 1);
            BigDecimal diasMesTotal = BigDecimal.valueOf(mesAtual.lengthOfMonth());
            BigDecimal proporcaoMes = diasNoMes.divide(diasMesTotal, 6, RoundingMode.HALF_UP);
            
            BigDecimal valorJurosMes = valorAcumulado
                    .multiply(taxaJuros)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                    .multiply(proporcaoMes);
            
            jurosTotal = jurosTotal.add(valorJurosMes);
            
            // Atualizar valor acumulado para próximo mês (juros capitalizados)
            valorAcumulado = valorAcumulado.add(valorJurosMes);
            
            mesAtual = mesAtual.plusMonths(1);
        }
        
        resultado.valorJuros = jurosTotal;
        resultado.taxaJurosAplicada = taxaDescricao.toString();
        resultado.fatorJuros = valorAcumulado.divide(valorBase, 6, RoundingMode.HALF_UP);
        
        return resultado;
    }
    
    /**
     * Formata período para exibição
     */
    private String formatarPeriodo(LocalDate inicio, LocalDate fim) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return inicio.format(formatter) + " a " + fim.format(formatter);
    }
    
    /**
     * Classe interna para resultado de cálculo de juros
     */
    private static class JurosResultado {
        BigDecimal valorJuros;
        String taxaJurosAplicada;
        BigDecimal fatorJuros;
    }
}

