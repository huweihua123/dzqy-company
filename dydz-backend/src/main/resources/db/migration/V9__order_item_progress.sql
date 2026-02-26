alter table production_records add column if not exists order_item_id bigint;
update production_records pr
set order_item_id = (
    select oi.id
    from order_items oi
    where oi.order_id = pr.order_id
    order by oi.id
    limit 1
)
where pr.order_item_id is null;

delete from production_records where order_item_id is null;

alter table production_records alter column order_item_id set not null;
create index if not exists idx_production_order_item on production_records(order_item_id);

alter table order_items add column if not exists done_piece_count numeric(12,3) not null default 0;
alter table order_items add column if not exists done_gross_tonnage numeric(12,3) not null default 0;
alter table order_items add column if not exists done_net_tonnage numeric(12,3) not null default 0;
