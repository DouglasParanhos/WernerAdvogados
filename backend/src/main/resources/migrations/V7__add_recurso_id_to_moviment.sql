ALTER TABLE moviment ADD COLUMN recurso_id BIGINT REFERENCES recurso(recurso_id) ON DELETE SET NULL;
