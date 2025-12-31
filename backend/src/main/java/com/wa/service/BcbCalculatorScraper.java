package com.wa.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class BcbCalculatorScraper {
    
    private static final String BCB_FORM_URL = "https://www3.bcb.gov.br/CALCIDADAO/publico/exibirFormCorrecaoValores.do?method=exibirFormCorrecaoValores&aba=4";
    
    /**
     * Formata data para o formato DDMMYYYY (sem barras)
     * Exemplo: 31/12/2025 -> 31122025
     */
    private String formatDateDDMMYYYY(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return date.format(formatter);
    }
    
    /**
     * Obtém o fator de correção SELIC da Calculadora do Cidadão do BCB
     * Usando Selenium para interagir com a página web
     */
    public BigDecimal getSelicFactorFromCalculator(LocalDate dataInicio, LocalDate dataFim) {
        WebDriver driver = null;
        try {
            // Formatar datas para o formato esperado pelo formulário (DDMMYYYY sem barras)
            String dataInicioStr = formatDateDDMMYYYY(dataInicio);
            String dataFimStr = formatDateDDMMYYYY(dataFim);
            
            log.info("Buscando fator SELIC da Calculadora do Cidadão: {} a {}", dataInicioStr, dataFimStr);
            
            // Configurar Chrome em modo headless
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--disable-extensions");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            
            // Usar Chrome do sistema se disponível (Docker)
            String chromeBin = System.getenv("CHROME_BIN");
            if (chromeBin != null && !chromeBin.isEmpty()) {
                options.setBinary(chromeBin);
            }
            
            driver = new ChromeDriver(options);
            
            // Navegar para a página do formulário
            driver.get(BCB_FORM_URL);
            
            // Aguardar a página carregar
            waitPageToLoad(driver, 10);
            
            // Preencher campos do formulário
            findElementByNameAndEnterValue(driver, "dataInicial", dataInicioStr);
            findElementByNameAndEnterValue(driver, "dataFinal", dataFimStr);
            findElementByNameAndEnterValue(driver, "valorCorrecao", "10000");
            
            // Clicar no botão de submit
            WebElement submitButton = driver.findElement(By.cssSelector("[value='Corrigir valor']"));
            submitButton.click();
            
            // Aguardar a página de resultado carregar
            waitPageToLoad(driver, 10);
            
            // Extrair o valor do índice de correção
            // Usando o XPath do exemplo fornecido
            WebElement indexElement = driver.findElement(By.xpath("/html/body/div[6]/table/tbody/tr/td/form/div[2]/table[1]/tbody/tr[6]/td[2]"));
            String factorText = indexElement.getText().trim();
            
            log.info("Fator SELIC encontrado: {}", factorText);
            
            // Normalizar formato: remover pontos de milhar e converter vírgula para ponto
            factorText = factorText.replaceAll("\\.", "").replace(",", ".");
            
            BigDecimal factor = new BigDecimal(factorText);
            log.info("Fator SELIC convertido com sucesso: {}", factor);
            return factor;
            
        } catch (Exception e) {
            log.error("Erro ao fazer scraping da Calculadora do Cidadão: {}", e.getMessage(), e);
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
     * Obtém o fator SELIC do período padrão (09/12/2021 até hoje)
     */
    public BigDecimal getSelicFactorDefaultPeriod() {
        LocalDate dataFim = LocalDate.now();
        LocalDate dataInicio = LocalDate.of(2021, 12, 9);
        return getSelicFactorFromCalculator(dataInicio, dataFim);
    }
    
    private void findElementByNameAndEnterValue(WebDriver driver, String name, String value) {
        WebElement element = driver.findElement(By.name(name));
        element.click();
        element.clear();
        element.sendKeys(value);
    }
    
    private void waitPageToLoad(WebDriver driver, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(webDriver -> {
            String readyState = (String) ((org.openqa.selenium.JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState");
            return "complete".equals(readyState);
        });
    }
}
