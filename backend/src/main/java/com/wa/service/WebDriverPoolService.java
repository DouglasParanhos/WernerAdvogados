package com.wa.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Serviço para gerenciar um pool de WebDrivers reutilizáveis
 * para otimizar o processamento de múltiplos processos
 */
@Service
@Slf4j
public class WebDriverPoolService {

    @Value("${bcb.selenium.headless:true}")
    private boolean headless;

    @Value("${bcb.selenium.pool.size:3}")
    private int poolSize;

    @Value("${bcb.selenium.pool.max-idle-time.seconds:300}")
    private int maxIdleTimeSeconds;

    private final BlockingQueue<WebDriver> driverPool = new LinkedBlockingQueue<>();
    private final AtomicInteger createdDrivers = new AtomicInteger(0);
    private volatile boolean shutdown = false;

    /**
     * Obtém um WebDriver do pool ou cria um novo se necessário
     */
    public WebDriver acquireDriver() {
        if (shutdown) {
            throw new IllegalStateException("WebDriverPool está sendo desligado");
        }

        WebDriver driver = driverPool.poll();
        if (driver == null) {
            driver = createNewDriver();
        } else {
            // Verificar se o driver ainda está válido
            try {
                driver.getCurrentUrl();
            } catch (Exception e) {
                log.debug("Driver do pool inválido, criando novo: {}", e.getMessage());
                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
                driver = createNewDriver();
            }
        }
        return driver;
    }

    /**
     * Retorna um WebDriver ao pool para reutilização
     */
    public void releaseDriver(WebDriver driver) {
        if (shutdown || driver == null) {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    log.debug("Erro ao fechar driver durante shutdown: {}", e.getMessage());
                }
            }
            return;
        }

        try {
            // Limpar estado do driver (navegar para página em branco)
            driver.get("about:blank");
            
            // Verificar se o pool não está cheio
            if (driverPool.size() < poolSize) {
                driverPool.offer(driver);
            } else {
                // Pool cheio, fechar o driver
                driver.quit();
                createdDrivers.decrementAndGet();
            }
        } catch (Exception e) {
            log.warn("Erro ao retornar driver ao pool, fechando: {}", e.getMessage());
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
            createdDrivers.decrementAndGet();
        }
    }

    /**
     * Cria um novo WebDriver com configurações otimizadas
     */
    private WebDriver createNewDriver() {
        ChromeOptions options = new ChromeOptions();

        // Configurar para usar Chromium do sistema (Alpine Linux)
        String chromeBinary = System.getenv().getOrDefault("CHROME_BIN", "/usr/bin/chromium-browser");
        options.setBinary(chromeBinary);

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--run-all-compositor-stages-before-draw");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");
        }

        // Configurações essenciais para performance e estabilidade
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--lang=pt-BR");
        options.addArguments("--force-color-profile=srgb");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--disable-ipc-flooding-protection");
        
        // Otimizações adicionais para performance (sem desabilitar imagens completamente para não quebrar a página)
        options.addArguments("--disable-javascript-harmony-shipping");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-plugins-discovery");

        // Configurar caminho do ChromeDriver
        String chromedriverPath = System.getenv().getOrDefault("CHROMEDRIVER_PATH", "/usr/bin/chromedriver");
        if (chromedriverPath != null && !chromedriverPath.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", chromedriverPath);
        } else {
            try {
                WebDriverManager.chromedriver().setup();
            } catch (Exception e) {
                log.warn("WebDriverManager não conseguiu configurar ChromeDriver, tentando usar o do sistema");
            }
        }

        WebDriver driver = new ChromeDriver(options);
        createdDrivers.incrementAndGet();
        log.debug("Novo WebDriver criado. Total criados: {}", createdDrivers.get());
        
        return driver;
    }

    /**
     * Limpa drivers ociosos do pool
     */
    public void cleanupIdleDrivers() {
        if (shutdown) {
            return;
        }

        int removed = 0;
        BlockingQueue<WebDriver> tempQueue = new LinkedBlockingQueue<>();
        
        while (!driverPool.isEmpty()) {
            try {
                WebDriver driver = driverPool.poll(100, TimeUnit.MILLISECONDS);
                if (driver != null) {
                    try {
                        driver.getCurrentUrl();
                        tempQueue.offer(driver);
                    } catch (Exception e) {
                        // Driver inválido, fechar
                        try {
                            driver.quit();
                            createdDrivers.decrementAndGet();
                            removed++;
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Retornar drivers válidos ao pool
        while (!tempQueue.isEmpty()) {
            WebDriver driver = tempQueue.poll();
            if (driver != null) {
                driverPool.offer(driver);
            }
        }

        if (removed > 0) {
            log.debug("Removidos {} drivers inválidos do pool", removed);
        }
    }

    /**
     * Fecha todos os drivers ao desligar a aplicação
     */
    @PreDestroy
    public void shutdown() {
        log.info("Desligando WebDriverPool...");
        shutdown = true;

        int closed = 0;
        while (!driverPool.isEmpty()) {
            try {
                WebDriver driver = driverPool.poll(100, TimeUnit.MILLISECONDS);
                if (driver != null) {
                    try {
                        driver.quit();
                        closed++;
                        createdDrivers.decrementAndGet();
                    } catch (Exception e) {
                        log.debug("Erro ao fechar driver durante shutdown: {}", e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("WebDriverPool desligado. {} drivers fechados", closed);
    }

    /**
     * Retorna estatísticas do pool
     */
    public PoolStats getStats() {
        return new PoolStats(
            driverPool.size(),
            createdDrivers.get(),
            poolSize
        );
    }

    public static class PoolStats {
        private final int available;
        private final int totalCreated;
        private final int maxSize;

        public PoolStats(int available, int totalCreated, int maxSize) {
            this.available = available;
            this.totalCreated = totalCreated;
            this.maxSize = maxSize;
        }

        public int getAvailable() {
            return available;
        }

        public int getTotalCreated() {
            return totalCreated;
        }

        public int getMaxSize() {
            return maxSize;
        }
    }
}

