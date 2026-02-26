ALTER TABLE production_records
    ADD COLUMN worker_id BIGINT;

ALTER TABLE production_records
    ADD CONSTRAINT fk_production_records_worker_id
    FOREIGN KEY (worker_id) REFERENCES workers(id);
