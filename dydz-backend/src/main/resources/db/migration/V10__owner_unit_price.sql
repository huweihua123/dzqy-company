ALTER TABLE owners
    ADD COLUMN unit_price NUMERIC(12, 2);

ALTER TABLE orders
    DROP COLUMN unit_price;

ALTER TABLE order_items
    DROP COLUMN unit_price;
