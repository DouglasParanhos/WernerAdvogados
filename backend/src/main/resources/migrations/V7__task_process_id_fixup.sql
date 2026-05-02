-- Correção quando histórico Flyway antigo marcou V4 como outro script e process_id nunca foi aplicado.
-- Idempotente (mesma lógica que V4__add_task_process_id.sql).

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'task'
          AND column_name = 'process_id'
    ) THEN
        ALTER TABLE public.task ADD COLUMN process_id BIGINT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_schema = 'public'
          AND table_name = 'task'
          AND constraint_name = 'task_process_id_fkey'
          AND constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE public.task
            ADD CONSTRAINT task_process_id_fkey
            FOREIGN KEY (process_id) REFERENCES public.process (process_id);
    END IF;
END $$;

COMMIT;
