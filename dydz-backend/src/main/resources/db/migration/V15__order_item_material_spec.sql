ALTER TABLE orders
    DROP COLUMN material_id;

ALTER TABLE orders
    DROP COLUMN spec_id;

ALTER TABLE order_items
    ADD COLUMN material_id BIGINT,
    ADD COLUMN spec_id BIGINT;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_material_id
    FOREIGN KEY (material_id) REFERENCES materials(id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_spec_id
    FOREIGN KEY (spec_id) REFERENCES specs(id);
