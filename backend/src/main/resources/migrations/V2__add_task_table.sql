-- Script para adicionar a tabela task ao banco de dados existente
-- Idempotente: seguro para executar mesmo se a tabela já existir

BEGIN;

-- Criar sequência se não existir
CREATE SEQUENCE IF NOT EXISTS public.task_task_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Criar tabela se não existir
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

-- Configurar sequência apenas se a tabela existir e a sequência ainda não estiver vinculada
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'task' AND table_schema = 'public') THEN
        -- Verificar se a sequência já está vinculada à tabela
        IF NOT EXISTS (
            SELECT 1 FROM pg_depend d
            JOIN pg_class c ON d.objid = c.oid
            JOIN pg_attribute a ON a.attrelid = c.oid AND a.attnum = d.objsubid
            WHERE d.refobjid = 'public.task_task_id_seq'::regclass::oid
            AND c.relname = 'task'
            AND a.attname = 'task_id'
        ) THEN
            ALTER SEQUENCE public.task_task_id_seq OWNED BY public.task.task_id;
        END IF;
        
        -- Configurar default apenas se ainda não estiver configurado
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'task' 
            AND table_schema = 'public'
            AND column_name = 'task_id'
            AND column_default LIKE '%task_task_id_seq%'
        ) THEN
            ALTER TABLE ONLY public.task ALTER COLUMN task_id SET DEFAULT nextval('public.task_task_id_seq'::regclass);
        END IF;
        
        -- Adicionar chave primária apenas se não existir
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints 
            WHERE table_name = 'task' 
            AND table_schema = 'public'
            AND constraint_name = 'task_pkey'
            AND constraint_type = 'PRIMARY KEY'
        ) THEN
            ALTER TABLE ONLY public.task ADD CONSTRAINT task_pkey PRIMARY KEY (task_id);
        END IF;
    END IF;
END $$;

COMMIT;

