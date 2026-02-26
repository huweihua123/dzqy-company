ALTER TABLE materials
    ADD COLUMN multiplier NUMERIC(12, 2) NOT NULL DEFAULT 1;

UPDATE materials SET multiplier = 1 WHERE multiplier IS NULL;
