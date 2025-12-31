package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelaCalculadaDTO {
    private String mesAno;
    private LocalDate dataVencimento;
    private BigDecimal valorBase;
    private String indiceCorrecaoMonetaria;
    private BigDecimal fatorCorrecaoMonetaria;
    private BigDecimal valorCorrigidoAte08122021;
    private BigDecimal fatorSelic; // Fator de correção SELIC
    private BigDecimal valorAtualizadoSelic;
    private BigDecimal descontoPrevidencia; // -11% de previdência
    private BigDecimal valorJurosMora;
    private BigDecimal valorTotalDevido;
    // Informações adicionais (serão exibidas abaixo do cálculo)
    private LocalDate dataInicialJuros;
    private String periodoIncidenciaJuros;
    private String taxaJurosAplicada;
    private BigDecimal fatorJuros;
}

