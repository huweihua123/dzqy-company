create table if not exists owner_receipts (
    id bigserial primary key,
    owner_id bigint not null references owners(id) on delete cascade,
    pay_date date not null,
    amount numeric(12,2) not null,
    remark varchar(300),
    created_at timestamp not null default now()
);

create index if not exists idx_owner_receipts_owner on owner_receipts(owner_id, pay_date);
