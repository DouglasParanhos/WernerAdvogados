package com.wa.service;

import com.wa.model.Process;
import com.wa.repository.ProcessRepository;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

    @Value("${process.correction.batch.size:20}")
    private int batchSize;

    @Value("${process.correction.parallel.enabled:true}")
    private boolean parallelEnabled;

    @Value("${process.correction.parallel.threads:3}")
    private int parallelThreads;

    /**
     * Recalcula e atualiza os valores corrigidos de todos os processos
     * que possuem valorOriginal e distribuidoEm não nulos
     * Processa em lotes para economizar memória
     */
    @Async("taskExecutor")
    public void recalculateAllProcessValues() {
        try {
            processUpdateStatusService.startProcessing();
            log.info("Iniciando recálculo de valores corrigidos de todos os processos (batch size: {})", batchSize);
            LocalDate dataFinal = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

            // Buscar apenas IDs dos processos que precisam de correção (query leve)
            List<Long> processIds = processRepository.findIdsForCorrection();

            if (processIds.isEmpty()) {
                log.info("Nenhum processo encontrado para correção");
                processUpdateStatusService.completeProcessing();
                return;
            }

            log.info("Encontrados {} processos para correção", processIds.size());

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            AtomicInteger skippedCount = new AtomicInteger(0);

            // Processar em lotes para economizar memória
            int totalBatches = (int) Math.ceil((double) processIds.size() / batchSize);
            log.info("Processando {} processos em {} lotes de até {} processos cada",
                    processIds.size(), totalBatches, batchSize);

            for (int i = 0; i < processIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, processIds.size());
                List<Long> batchIds = processIds.subList(i, endIndex);
                int batchNumber = (i / batchSize) + 1;

                log.info("Processando lote {}/{} ({} processos)", batchNumber, totalBatches, batchIds.size());

                // Buscar processos do lote atual
                List<Process> batchProcesses = processRepository.findByIds(batchIds);

                // Processar o lote
                if (parallelEnabled && batchProcesses.size() > 1) {
                    processInParallel(batchProcesses, dataFinal, successCount, errorCount, skippedCount);
                } else {
                    processSequentially(batchProcesses, dataFinal, successCount, errorCount, skippedCount);
                }

                // Limpar cache do Hibernate para liberar memória
                entityManager.clear();

                log.debug("Lote {}/{} concluído. Memória liberada.", batchNumber, totalBatches);
            }

            log.info("Recálculo de valores corrigidos concluído. " +
                    "Sucesso: {}, Erros: {}, Ignorados: {}, Total: {}",
                    successCount.get(), errorCount.get(), skippedCount.get(), processIds.size());

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

        // Normalizar tipo de processo para comparação (case-insensitive)
        String tipoProcesso = process.getTipoProcesso() != null ? process.getTipoProcesso().toUpperCase().trim() : null;

        // Para NOVAESCOLA e INTERNIVEIS: valorCorrigido = valorOriginal (sem cálculo
        // BCB)
        if ("NOVAESCOLA".equals(tipoProcesso) || "INTERNIVEIS".equals(tipoProcesso)) {
            Double valorCorrigido = process.getValorOriginal();
            process.setValorCorrigido(valorCorrigido);
            processRepository.save(process);
            log.info("Valor corrigido do processo {} (tipo: {}) atualizado para R$ {} (igual ao valor original)",
                    processId, tipoProcesso, valorCorrigido);
            return true;
        }

        // Para PISO: calcular valor corrigido via BCB (requer data de distribuição)
        if (!"PISO".equals(tipoProcesso)) {
            log.warn("Processo {} tem tipo desconhecido: {}. Não é possível recalcular.", processId, tipoProcesso);
            return false;
        }

        if (process.getDistribuidoEm() == null) {
            log.warn("Processo {} (tipo: PISO) não possui data de distribuição", processId);
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
                log.info("Valor corrigido do processo {} (tipo: PISO) atualizado para R$ {}", processId,
                        valorCorrigido);
                return true;
            } else {
                log.warn("Não foi possível calcular valor corrigido para processo {} (tipo: PISO)", processId);
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
            // Normalizar tipo de processo para comparação (case-insensitive)
            String tipoProcesso = process.getTipoProcesso() != null ? process.getTipoProcesso().toUpperCase().trim()
                    : null;

            // Para NOVAESCOLA e INTERNIVEIS: valorCorrigido = valorOriginal (sem cálculo
            // BCB)
            if ("NOVAESCOLA".equals(tipoProcesso) || "INTERNIVEIS".equals(tipoProcesso)) {
                if (process.getValorOriginal() != null && process.getValorOriginal() > 0) {
                    Double valorCorrigido = process.getValorOriginal();
                    if (saveProcessValue(process.getId(), valorCorrigido)) {
                        successCount.incrementAndGet();
                        log.info("Processo {} (tipo: {}) atualizado: valor corrigido = valor original = R$ {}",
                                process.getId(), tipoProcesso, valorCorrigido);
                    } else {
                        errorCount.incrementAndGet();
                    }
                } else {
                    log.warn("Processo {} (tipo: {}) não possui valor original válido para atualização",
                            process.getId(), tipoProcesso);
                    errorCount.incrementAndGet();
                }
                return;
            }

            // Para PISO: calcular valor corrigido via BCB (comportamento original)
            if (!"PISO".equals(tipoProcesso)) {
                log.warn("Processo {} tem tipo desconhecido: {}. Pulando processamento.",
                        process.getId(), tipoProcesso);
                skippedCount.incrementAndGet();
                return;
            }

            LocalDate dataInicial = process.getDistribuidoEm().toLocalDate();

            // Validar se a data inicial não é futura
            if (dataInicial.isAfter(dataFinal)) {
                log.warn("Processo {} tem data de distribuição futura ({}), pulando",
                        process.getId(), dataInicial);
                skippedCount.incrementAndGet();
                return;
            }

            // Calcular valor corrigido via BCB para processos PISO
            log.debug("Calculando correção BCB para processo {} (tipo: PISO, valor: R$ {}, data: {})",
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
                    log.info("Processo {} (tipo: PISO) atualizado: valor corrigido = R$ {} (valor original = R$ {})",
                            process.getId(), valorCorrigido, process.getValorOriginal());
                } else {
                    errorCount.incrementAndGet();
                }
            } else {
                log.warn(
                        "Não foi possível calcular valor corrigido para processo {} (tipo: PISO, valor original: R$ {}, distribuído em: {})",
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
