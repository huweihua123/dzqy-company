alter table orders add column if not exists plan_piece_count numeric(12,3) not null default 0;
alter table orders add column if not exists done_piece_count numeric(12,3) not null default 0;
