package com.wa.service;

import com.wa.model.Process;
import com.wa.repository.ProcessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessValueCorrectionService {

    private final ProcessRepository processRepository;
    private final BCBCalculatorService bcbCalculatorService;
    private final ProcessUpdateStatusService processUpdateStatusService;
    private final TransactionTemplate transactionTemplate;

    @Value("${process.correction.batch.size:50}")
    private int batchSize;

    @Value("${process.correction.parallel.enabled:true}")
    private boolean parallelEnabled;

    @Value("${process.correction.parallel.threads:3}")
    private int parallelThreads;

    /**
     * Recalcula e atualiza os valores corrigidos de todos os processos
     * que possuem valorOriginal e distribuidoEm não nulos
     */
    @Async("taskExecutor")
    public void recalculateAllProcessValues() {
        try {
            processUpdateStatusService.startProcessing();
            log.info("Iniciando recálculo de valores corrigidos de todos os processos");
            LocalDate dataFinal = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

            // Buscar todos os processos que precisam de correção
            List<Process> processes = processRepository.findAll().stream()
                    .filter(p -> p.getValorOriginal() != null && p.getValorOriginal() > 0)
                    .filter(p -> p.getDistribuidoEm() != null)
                    .toList();

            if (processes.isEmpty()) {
                log.info("Nenhum processo encontrado para correção");
                processUpdateStatusService.completeProcessing();
                return;
            }

            log.info("Encontrados {} processos para correção", processes.size());

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            AtomicInteger skippedCount = new AtomicInteger(0);

            if (parallelEnabled && processes.size() > 1) {
                // Processamento paralelo otimizado
                log.info("Processando {} processos em paralelo usando {} threads", processes.size(), parallelThreads);
                processInParallel(processes, dataFinal, successCount, errorCount, skippedCount);
            } else {
                // Processamento sequencial (fallback ou quando paralelo está desabilitado)
                log.info("Processando {} processos sequencialmente", processes.size());
                processSequentially(processes, dataFinal, successCount, errorCount, skippedCount);
            }

            log.info("Recálculo de valores corrigidos concluído. " +
                    "Sucesso: {}, Erros: {}, Ignorados: {}, Total: {}",
                    successCount.get(), errorCount.get(), skippedCount.get(), processes.size());

            processUpdateStatusService.completeProcessing();
        } catch (Exception e) {
            log.error("Erro fatal ao executar recálculo de valores corrigidos", e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
            processUpdateStatusService.errorProcessing(errorMessage);
        }
    }

    /**
     * Recalcula o valor corrigido de um processo específico
     */
    @Transactional
    public boolean recalculateProcessValue(Long processId) {
        Process process = processRepository.findById(processId)
                .orElse(null);

        if (process == null) {
            log.warn("Processo {} não encontrado", processId);
            return false;
        }

        if (process.getValorOriginal() == null || process.getValorOriginal() <= 0) {
            log.warn("Processo {} não possui valor original válido", processId);
            return false;
        }

        if (process.getDistribuidoEm() == null) {
            log.warn("Processo {} não possui data de distribuição", processId);
            return false;
        }

        LocalDate dataInicial = process.getDistribuidoEm().toLocalDate();
        LocalDate dataFinal = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        if (dataInicial.isAfter(dataFinal)) {
            log.warn("Processo {} tem data de distribuição futura", processId);
            return false;
        }

        try {
            Double valorCorrigido = bcbCalculatorService.calculateSelicCorrection(
                    dataInicial,
                    dataFinal,
                    process.getValorOriginal());

            if (valorCorrigido != null && valorCorrigido > 0) {
                process.setValorCorrigido(valorCorrigido);
                processRepository.save(process);
                log.info("Valor corrigido do processo {} atualizado para R$ {}", processId, valorCorrigido);
                return true;
            } else {
                log.warn("Não foi possível calcular valor corrigido para processo {}", processId);
                return false;
            }
        } catch (Exception e) {
            log.error("Erro ao recalcular valor do processo {}: {}", processId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Processa processos em paralelo usando ExecutorService
     */
    private void processInParallel(List<Process> processes, LocalDate dataFinal,
            AtomicInteger successCount, AtomicInteger errorCount, AtomicInteger skippedCount) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelThreads);

        try {
            List<CompletableFuture<Void>> futures = processes.stream()
                    .map(process -> CompletableFuture.runAsync(() -> {
                        processSingleProcess(process, dataFinal, successCount, errorCount, skippedCount);
                    }, executor))
                    .collect(Collectors.toList());

            // Aguardar conclusão de todos os processos
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Processa processos sequencialmente
     */
    private void processSequentially(List<Process> processes, LocalDate dataFinal,
            AtomicInteger successCount, AtomicInteger errorCount, AtomicInteger skippedCount) {
        for (Process process : processes) {
            processSingleProcess(process, dataFinal, successCount, errorCount, skippedCount);
        }
    }

    /**
     * Processa um único processo (thread-safe)
     */
    private void processSingleProcess(Process process, LocalDate dataFinal,
            AtomicInteger successCount, AtomicInteger errorCount, AtomicInteger skippedCount) {
        try {
            LocalDate dataInicial = process.getDistribuidoEm().toLocalDate();

            // Validar se a data inicial não é futura
            if (dataInicial.isAfter(dataFinal)) {
                log.warn("Processo {} tem data de distribuição futura ({}), pulando",
                        process.getId(), dataInicial);
                skippedCount.incrementAndGet();
                return;
            }

            // Calcular valor corrigido
            log.debug("Calculando correção para processo {} (valor: R$ {}, data: {})",
                    process.getId(), process.getValorOriginal(), dataInicial);

            Double valorCorrigido = null;
            try {
                valorCorrigido = bcbCalculatorService.calculateSelicCorrection(
                        dataInicial,
                        dataFinal,
                        process.getValorOriginal());
            } catch (Exception calcException) {
                log.error("Erro ao calcular correção Selic para processo {}: {}",
                        process.getId(), calcException.getMessage(), calcException);
                errorCount.incrementAndGet();
                return;
            }

            if (valorCorrigido != null && valorCorrigido > 0) {
                // Chamar método transacional para salvar
                if (saveProcessValue(process.getId(), valorCorrigido)) {
                    successCount.incrementAndGet();
                    log.info("Processo {} atualizado: valor corrigido = R$ {} (valor original = R$ {})",
                            process.getId(), valorCorrigido, process.getValorOriginal());
                } else {
                    errorCount.incrementAndGet();
                }
            } else {
                log.warn(
                        "Não foi possível calcular valor corrigido para processo {} (valor original: R$ {}, distribuído em: {})",
                        process.getId(), process.getValorOriginal(), dataInicial);
                errorCount.incrementAndGet();
            }

        } catch (Exception e) {
            log.error("Erro ao processar correção do processo {}: {}",
                    process.getId(), e.getMessage(), e);
            errorCount.incrementAndGet();
        }
    }

    /**
     * Salva o valor corrigido do processo em uma transação separada
     * Usa TransactionTemplate para garantir que funcione em contextos assíncronos
     */
    public boolean saveProcessValue(Long processId, Double valorCorrigido) {
        try {
            return transactionTemplate.execute(status -> {
                try {
                    Process processToUpdate = processRepository.findById(processId)
                            .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + processId));
                    processToUpdate.setValorCorrigido(valorCorrigido);
                    processRepository.saveAndFlush(processToUpdate);
                    log.debug("Processo {} salvo com valor corrigido R$ {}", processId, valorCorrigido);
                    return true;
                } catch (Exception e) {
                    log.error("Erro ao salvar valor corrigido para processo {}: {}",
                            processId, e.getMessage(), e);
                    status.setRollbackOnly();
                    return false;
                }
            });
        } catch (Exception saveException) {
            log.error("Erro ao executar transação para processo {}: {}",
                    processId, saveException.getMessage(), saveException);
            return false;
        }
    }
}
