-- Script para adicionar coluna visible_to_client na tabela moviment
-- Idempotente: seguro para executar mesmo se a coluna já existir

BEGIN;

-- Adicionar coluna visible_to_client se não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'moviment' 
        AND table_schema = 'public'
        AND column_name = 'visible_to_client'
    ) THEN
        ALTER TABLE public.moviment 
        ADD COLUMN visible_to_client BOOLEAN NOT NULL DEFAULT true;
        
        -- Atualizar registros existentes para true (retrocompatibilidade)
        UPDATE public.moviment 
        SET visible_to_client = true 
        WHERE visible_to_client IS NULL;
    END IF;
END $$;

COMMIT;

