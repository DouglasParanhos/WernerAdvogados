package com.wa.service;

import com.wa.dto.ParcelaCalculadaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelGenerationService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Gera planilha Excel com os cálculos NOVAESCOLA
     */
    public byte[] generateNovaEscolaExcel(List<ParcelaCalculadaDTO> parcelas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cálculo NOVAESCOLA");
            
            // Criar estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle wrapTextStyle = createWrapTextStyle(workbook);
            CellStyle dateWrapStyle = createDateWrapStyle(workbook);
            
            int rowNum = 0;
            
            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("CÁLCULO JUDICIAL - NOVAESCOLA");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
            
            // Linha em branco
            rowNum++;
            
            // Cabeçalhos das colunas
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "Mês/Ano da Parcela",
                "Valor Base da Parcela",
                "Data de Vencimento",
                "Índice de Correção Monetária Aplicado",
                "Fator de Correção Monetária",
                "Valor Corrigido até 30/11/2021",
                "Fator de Correção SELIC (a partir de 09/12/2021)",
                "Valor Atualizado pela SELIC",
                "Valor dos Juros de Mora",
                "Valor Total Devido da Parcela"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Dados das parcelas
            BigDecimal totalGeral = BigDecimal.ZERO;
            for (ParcelaCalculadaDTO parcela : parcelas) {
                Row dataRow = sheet.createRow(rowNum++);
                
                // Mês/Ano da Parcela
                createCell(dataRow, 0, parcela.getMesAno(), null);
                
                // Valor Base da Parcela
                createCell(dataRow, 1, parcela.getValorBase(), currencyStyle);
                
                // Data de Vencimento
                if (parcela.getDataVencimento() != null) {
                    createCell(dataRow, 2, parcela.getDataVencimento().format(DATE_FORMATTER), dateStyle);
                }
                
                // Índice de Correção Monetária Aplicado
                createCell(dataRow, 3, parcela.getIndiceCorrecaoMonetaria(), null);
                
                // Fator de Correção Monetária
                createCell(dataRow, 4, parcela.getFatorCorrecaoMonetaria(), null);
                
                // Valor Corrigido até 30/11/2021
                createCell(dataRow, 5, parcela.getValorCorrigidoAte08122021(), currencyStyle);
                
                // Fator de Correção SELIC (a partir de 09/12/2021)
                createCell(dataRow, 6, parcela.getFatorSelic(), null);
                
                // Valor Atualizado pela SELIC
                createCell(dataRow, 7, parcela.getValorAtualizadoSelic(), currencyStyle);
                
                // Valor dos Juros de Mora
                createCell(dataRow, 8, parcela.getValorJurosMora(), currencyStyle);
                
                // Valor Total Devido da Parcela
                createCell(dataRow, 9, parcela.getValorTotalDevido(), currencyStyle);
                
                totalGeral = totalGeral.add(parcela.getValorTotalDevido());
            }
            
            // Linha em branco
            rowNum++;
            
            // Total Geral
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(8);
            totalLabelCell.setCellValue("TOTAL GERAL DEVIDO:");
            totalLabelCell.setCellStyle(totalStyle);
            
            Cell totalValueCell = totalRow.createCell(9);
            totalValueCell.setCellValue(totalGeral.doubleValue());
            totalValueCell.setCellStyle(totalStyle);
            
            // Desconto Previdenciário (11% do total geral)
            BigDecimal descontoPrevidencia = totalGeral
                    .multiply(BigDecimal.valueOf(11))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            Row descontoRow = sheet.createRow(rowNum++);
            Cell descontoLabelCell = descontoRow.createCell(8);
            descontoLabelCell.setCellValue("Desconto previdenciário:");
            descontoLabelCell.setCellStyle(totalStyle);
            
            Cell descontoValueCell = descontoRow.createCell(9);
            descontoValueCell.setCellValue(descontoPrevidencia.doubleValue());
            descontoValueCell.setCellStyle(totalStyle);
            
            // Total Descontado Geral
            BigDecimal totalDescontado = totalGeral.subtract(descontoPrevidencia);
            
            Row totalDescontadoRow = sheet.createRow(rowNum++);
            Cell totalDescontadoLabelCell = totalDescontadoRow.createCell(8);
            totalDescontadoLabelCell.setCellValue("Total descontado geral:");
            totalDescontadoLabelCell.setCellStyle(totalStyle);
            
            Cell totalDescontadoValueCell = totalDescontadoRow.createCell(9);
            totalDescontadoValueCell.setCellValue(totalDescontado.doubleValue());
            totalDescontadoValueCell.setCellStyle(totalStyle);
            
            // Honorários Sucumbenciais (10% do total geral)
            BigDecimal honorarios = totalGeral
                    .multiply(BigDecimal.valueOf(10))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            Row honorariosRow = sheet.createRow(rowNum++);
            Cell honorariosLabelCell = honorariosRow.createCell(8);
            honorariosLabelCell.setCellValue("Honorários sucumbenciais:");
            honorariosLabelCell.setCellStyle(totalStyle);
            
            Cell honorariosValueCell = honorariosRow.createCell(9);
            honorariosValueCell.setCellValue(honorarios.doubleValue());
            honorariosValueCell.setCellStyle(totalStyle);
            
            // Linhas em branco (5 linhas)
            for (int i = 0; i < 5; i++) {
                rowNum++;
            }
            
            // Informações adicionais sobre juros (5 linhas abaixo do cálculo)
            
            // Cabeçalho para informações de juros
            Row infoHeaderRow = sheet.createRow(rowNum++);
            Cell infoHeaderCell = infoHeaderRow.createCell(0);
            infoHeaderCell.setCellValue("INFORMAÇÕES SOBRE JUROS DE MORA:");
            infoHeaderCell.setCellStyle(headerStyle);
            
            // Data Inicial dos Juros
            if (!parcelas.isEmpty() && parcelas.get(0).getDataInicialJuros() != null) {
                Row dataInicialRow = sheet.createRow(rowNum++);
                createCell(dataInicialRow, 0, "Data Inicial dos Juros:", wrapTextStyle);
                createCell(dataInicialRow, 1, parcelas.get(0).getDataInicialJuros().format(DATE_FORMATTER), dateWrapStyle);
            }
            
            // Período de Incidência dos Juros
            if (!parcelas.isEmpty() && parcelas.get(0).getPeriodoIncidenciaJuros() != null) {
                Row periodoRow = sheet.createRow(rowNum++);
                createCell(periodoRow, 0, "Período de Incidência dos Juros:", wrapTextStyle);
                createCell(periodoRow, 1, parcelas.get(0).getPeriodoIncidenciaJuros(), wrapTextStyle);
            }
            
            // Taxa de Juros Aplicada
            if (!parcelas.isEmpty() && parcelas.get(0).getTaxaJurosAplicada() != null) {
                Row taxaRow = sheet.createRow(rowNum++);
                createCell(taxaRow, 0, "Taxa de Juros Aplicada:", wrapTextStyle);
                createCell(taxaRow, 1, parcelas.get(0).getTaxaJurosAplicada(), wrapTextStyle);
            }
            
            // Fator de Juros
            if (!parcelas.isEmpty() && parcelas.get(0).getFatorJuros() != null) {
                Row fatorRow = sheet.createRow(rowNum++);
                createCell(fatorRow, 0, "Fator de Juros:", wrapTextStyle);
                createCell(fatorRow, 1, parcelas.get(0).getFatorJuros(), wrapTextStyle);
            }
            
            // Ajustar largura das colunas para 2x a largura padrão do Excel
            // A largura padrão do Excel é aproximadamente 8.43 caracteres (2048 unidades no Apache POI)
            // 2x a largura padrão = aproximadamente 4096 unidades
            int larguraPadrao = 2048; // Largura padrão do Excel
            int larguraDesejada = larguraPadrao * 2; // 2x a largura padrão
            
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, larguraDesejada);
            }
            
            // Converter para byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
        
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Formatar número com 2 casas decimais se necessário
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.format("%.2f", numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        
        return style;
    }
    
    private CellStyle createWrapTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDateWrapStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}

