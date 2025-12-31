-- Script de migração para renomear campo valor para valor_original e adicionar valor_corrigido
-- Idempotente: seguro para executar mesmo se as mudanças já foram aplicadas

BEGIN;

DO $$
BEGIN
    -- Verificar se a tabela process existe
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'process' AND table_schema = 'public') THEN
        
        -- Renomear coluna valor para valor_original apenas se valor ainda existir e valor_original não existir
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'process' 
            AND table_schema = 'public'
            AND column_name = 'valor'
        ) AND NOT EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'process' 
            AND table_schema = 'public'
            AND column_name = 'valor_original'
        ) THEN
            ALTER TABLE public.process RENAME COLUMN valor TO valor_original;
        END IF;
        
        -- Adicionar coluna valor_corrigido apenas se não existir
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'process' 
            AND table_schema = 'public'
            AND column_name = 'valor_corrigido'
        ) THEN
            ALTER TABLE public.process ADD COLUMN valor_corrigido double precision;
        END IF;
        
        -- Copiar valores de valor_original para valor_corrigido onde valor_corrigido for NULL
        UPDATE public.process 
        SET valor_corrigido = valor_original 
        WHERE valor_original IS NOT NULL 
        AND valor_corrigido IS NULL;
        
    END IF;
END $$;

COMMIT;

