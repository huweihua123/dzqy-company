alter table orders add column if not exists production_status varchar(20) not null default 'NOT_STARTED';
create index if not exists idx_orders_production_status on orders(production_status);
