package com.wa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "datajud.api")
public class DatajudProperties {

    /**
     * TJRJ public API search endpoint.
     */
    private String url = "https://api-publica.datajud.cnj.jus.br/api_publica_tjrj/_search";

    private int timeoutMs = 20_000;

    private int maxRetries = 3;

    private double backoffBaseSeconds = 1.5;

    /**
     * CNJ DataJud API key ({@code DATAJUD_API_KEY}).
     */
    private String key = "";

    /**
     * Max Elasticsearch hits per NPU query (G1+G2 + duplicates). Capped when building the query.
     */
    private int maxHits = 10;

    /**
     * Max movements returned per grau after date filtering.
     */
    private int maxMovimentosPorGrau = 5;

    /**
     * Hard cap for {@link #maxHits} when read from config (avoids oversized requests).
     */
    private int maxHitsCeiling = 50;

    /**
     * Threads for batch movimentos ({@code consultarMovimentosDesde}): consulta NPUs em paralelo para reduzir tempo total.
     * Valores altos podem aumentar risco de rate limit na API CNJ.
     */
    private int movimentosBatchParallelism = 4;
}
