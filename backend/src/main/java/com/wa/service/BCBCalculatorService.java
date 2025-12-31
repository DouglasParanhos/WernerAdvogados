package com.wa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class BCBCalculatorService {

    private static final String BCB_CALCULATOR_URL = "https://www3.bcb.gov.br/CALCIDADAO/publico/exibirFormCorrecaoValores.do?method=exibirFormCorrecaoValores&aba=4";
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Value("${bcb.selenium.headless:true}")
    private boolean headless;

    /**
     * Formata data para o formato DDMMYYYY (sem barras)
     * Exemplo: 31/12/2025 -> 31122025
     */
    private String formatDateDDMMYYYY(LocalDate date) {
        return date.format(DATE_FORMATTER_DDMMYYYY);
    }

    /**
     * Calcula a correção Selic de um valor usando a Calculadora do Cidadão do Banco
     * Central
     * Usando Selenium para interagir com a página web (mesma abordagem do
     * BcbCalculatorScraper)
     * 
     * @param dataInicial   Data inicial (data de distribuição do processo)
     * @param dataFinal     Data final (data atual)
     * @param valorOriginal Valor a ser corrigido
     * @return Valor corrigido pela Selic, ou null se houver erro
     */
    public Double calculateSelicCorrection(LocalDate dataInicial, LocalDate dataFinal, Double valorOriginal) {
        if (dataInicial == null || dataFinal == null || valorOriginal == null || valorOriginal <= 0) {
            log.warn("Parâmetros inválidos para cálculo Selic: dataInicial={}, dataFinal={}, valorOriginal={}",
                    dataInicial, dataFinal, valorOriginal);
            return null;
        }

        // Validar que a data inicial seja a partir de 04/06/1986 (limite da calculadora
        // Selic)
        LocalDate minDate = LocalDate.of(1986, 6, 4);
        if (dataInicial.isBefore(minDate)) {
            log.warn("Data inicial {} é anterior ao limite mínimo da calculadora Selic (04/06/1986)", dataInicial);
            dataInicial = minDate;
        }

        WebDriver driver = null;
        try {
            // Formatar datas para o formato esperado pelo formulário (DDMMYYYY sem barras)
            String dataInicialStr = formatDateDDMMYYYY(dataInicial);
            String dataFinalStr = formatDateDDMMYYYY(dataFinal);

            // Formatar valor no padrão brasileiro: remover pontos de milhar e usar vírgula
            // como decimal
            String valorStr;
            if (valorOriginal % 1 == 0) {
                valorStr = String.format("%.0f", valorOriginal).replace(".", ",");
            } else {
                valorStr = String.format("%.2f", valorOriginal).replace(".", ",");
            }

            log.info("Buscando valor corrigido SELIC da Calculadora do Cidadão: {} a {}, valor: R$ {}",
                    dataInicialStr, dataFinalStr, valorStr);

            // Configurar Chrome em modo headless
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless");
            }
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--disable-extensions");
            options.addArguments(
                    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            // Usar Chrome do sistema se disponível (Docker)
            String chromeBin = System.getenv("CHROME_BIN");
            if (chromeBin != null && !chromeBin.isEmpty()) {
                options.setBinary(chromeBin);
            }

            driver = new ChromeDriver(options);

            // Navegar para a página do formulário
            driver.get(BCB_CALCULATOR_URL);

            // Aguardar a página carregar
            waitPageToLoad(driver, 10);

            // Preencher campos do formulário
            findElementByNameAndEnterValue(driver, "dataInicial", dataInicialStr);
            findElementByNameAndEnterValue(driver, "dataFinal", dataFinalStr);
            findElementByNameAndEnterValue(driver, "valorCorrecao", valorStr);

            // Clicar no botão de submit
            WebElement submitButton = driver.findElement(By.cssSelector("[value='Corrigir valor']"));
            submitButton.click();

            // Aguardar a página de resultado carregar
            waitPageToLoad(driver, 10);

            // Extrair o valor corrigido da página de resultado
            // O valor corrigido está na célula seguinte à célula com "Valor corrigido na
            // data final"
            WebElement valorCorrigidoElement = driver.findElement(
                    By.xpath(
                            "//td[contains(normalize-space(text()), 'Valor corrigido na data final')]/following-sibling::td[1]"));
            String valorCorrigidoText = valorCorrigidoElement.getText().trim();

            log.info("Valor corrigido encontrado: {}", valorCorrigidoText);

            // Converter para Double: remover "R$", pontos de milhar e converter vírgula
            // para ponto
            Double valorCorrigido = parseValue(valorCorrigidoText);

            if (valorCorrigido != null) {
                log.info("Valor corrigido convertido com sucesso: R$ {} (de R$ {} entre {} e {})",
                        valorCorrigido, valorOriginal, dataInicial, dataFinal);
            } else {
                log.error("Não foi possível converter o valor corrigido: {}", valorCorrigidoText);
            }

            return valorCorrigido;

        } catch (Exception e) {
            log.error("Erro ao fazer scraping da Calculadora do Cidadão para calcular correção Selic: {}",
                    e.getMessage(), e);
            return null;
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    log.warn("Erro ao fechar o driver: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Preenche um campo do formulário pelo nome
     */
    private void findElementByNameAndEnterValue(WebDriver driver, String name, String value) {
        WebElement element = driver.findElement(By.name(name));
        element.click();
        element.clear();
        element.sendKeys(value);
    }

    /**
     * Aguarda a página carregar completamente
     */
    private void waitPageToLoad(WebDriver driver, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(webDriver -> {
            String readyState = (String) ((org.openqa.selenium.JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState");
            return "complete".equals(readyState);
        });
    }

    /**
     * Converte string de valor monetário para Double
     * Exemplos: "1.234,56" -> 1234.56, "R$ 1.234,56" -> 1234.56, "R$ 1.603,24
     * (REAL)" -> 1603.24
     */
    private Double parseValue(String valueStr) {
        if (valueStr == null || valueStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Remover "R$" e espaços
            valueStr = valueStr.replace("R$", "").trim();
            // Remover texto entre parênteses como "(REAL)" que aparece no resultado do BCB
            valueStr = valueStr.replaceAll("\\([^)]*\\)", "").trim();
            // Remover pontos (separadores de milhar)
            valueStr = valueStr.replace(".", "");
            // Substituir vírgula por ponto (separador decimal)
            valueStr = valueStr.replace(",", ".");
            // Remover espaços restantes
            valueStr = valueStr.trim();

            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            log.warn("Não foi possível converter '{}' para número", valueStr);
            return null;
        }
    }
}
