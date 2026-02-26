CREATE TABLE worker_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    remark VARCHAR(300)
);

ALTER TABLE workers
    ADD COLUMN group_id BIGINT;

ALTER TABLE workers
    ADD CONSTRAINT fk_workers_group_id
    FOREIGN KEY (group_id) REFERENCES worker_groups(id);
