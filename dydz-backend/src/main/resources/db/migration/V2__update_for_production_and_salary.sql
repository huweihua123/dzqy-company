alter table workers drop column if exists level;
alter table workers drop column if exists base_salary;
alter table workers add column if not exists ton_price numeric(12,2);
alter table workers add column if not exists daily_salary numeric(12,2);

alter table orders drop column if exists cut_weight;
alter table orders add column if not exists plan_gross_tonnage numeric(12,3) not null default 0;
alter table orders add column if not exists plan_net_tonnage numeric(12,3) not null default 0;
alter table orders add column if not exists unit_price numeric(12,2) not null default 0;
alter table orders add column if not exists total_fee numeric(12,2) not null default 0;
alter table orders add column if not exists done_gross_tonnage numeric(12,3) not null default 0;
alter table orders add column if not exists done_net_tonnage numeric(12,3) not null default 0;

alter table order_items drop column if exists qty;
alter table order_items drop column if exists weight;
alter table order_items drop column if exists price;

alter table order_items add column if not exists piece_count numeric(12,3) not null default 0;
alter table order_items add column if not exists piece_weight numeric(12,3) not null default 0;
alter table order_items add column if not exists gross_tonnage numeric(12,3) not null default 0;
alter table order_items add column if not exists net_tonnage numeric(12,3) not null default 0;
alter table order_items add column if not exists unit_price numeric(12,2);

alter table production_records drop column if exists tonnage;
alter table production_records drop column if exists worker_id;
alter table production_records add column if not exists gross_tonnage numeric(12,3) not null default 0;
alter table production_records add column if not exists net_tonnage numeric(12,3) not null default 0;
alter table production_records add column if not exists piece_count numeric(12,3);
