-- Data final opcional/obrigatória conforme tipo da tarefa (validação na aplicação)
-- Idempotente

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'task'
          AND column_name = 'prazo_final'
    ) THEN
        ALTER TABLE public.task ADD COLUMN prazo_final DATE NULL;
    END IF;
END $$;

COMMIT;
