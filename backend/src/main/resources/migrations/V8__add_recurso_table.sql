CREATE TABLE recurso (
    recurso_id            BIGSERIAL PRIMARY KEY,
    process_id            BIGINT NOT NULL REFERENCES process(process_id),
    classe                VARCHAR(50) NOT NULL,
    numero                VARCHAR(100),
    desembargador_relator  VARCHAR(255),
    camara                VARCHAR(255),
    sistema               VARCHAR(50),
    status_recurso        VARCHAR(255),
    historico_relator     VARCHAR(30) NOT NULL DEFAULT 'NA',
    historico_camara      VARCHAR(30) NOT NULL DEFAULT 'NA',
    baixado               BOOLEAN NOT NULL DEFAULT false,
    created_on            TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_on           TIMESTAMP NOT NULL DEFAULT NOW()
);
