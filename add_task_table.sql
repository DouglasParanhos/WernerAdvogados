-- Script para adicionar a tabela task ao banco de dados existente
-- Execute este script se o banco já foi criado sem a tabela task

BEGIN;

-- Criar sequência
CREATE SEQUENCE IF NOT EXISTS public.task_task_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Criar tabela
CREATE TABLE IF NOT EXISTS public.task (
    task_id bigint NOT NULL,
    titulo character varying(255) NOT NULL,
    descricao character varying(1000),
    tipo_tarefa character varying(50) NOT NULL,
    status character varying(50) NOT NULL,
    responsavel character varying(50) NOT NULL,
    ordem integer,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Configurar sequência
ALTER SEQUENCE public.task_task_id_seq OWNED BY public.task.task_id;
ALTER TABLE ONLY public.task ALTER COLUMN task_id SET DEFAULT nextval('public.task_task_id_seq'::regclass);

-- Adicionar chave primária
ALTER TABLE ONLY public.task
    ADD CONSTRAINT task_pkey PRIMARY KEY (task_id);

COMMIT;




