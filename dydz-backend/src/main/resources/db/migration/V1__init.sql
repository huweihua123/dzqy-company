create table owners (
    id bigserial primary key,
    name varchar(100) not null,
    contact varchar(50),
    phone varchar(30),
    address varchar(200),
    remark varchar(500),
    created_at timestamp not null default now()
);

create table materials (
    id bigserial primary key,
    name varchar(100) not null,
    code varchar(50),
    remark varchar(300)
);

create table specs (
    id bigserial primary key,
    name varchar(100) not null,
    description varchar(300)
);

create table orders (
    id bigserial primary key,
    order_no varchar(50) not null unique,
    owner_id bigint not null references owners(id),
    material_id bigint references materials(id),
    spec_id bigint references specs(id),
    cut_weight numeric(12,3),
    order_date date not null,
    status varchar(20) not null,
    receivable_amount numeric(12,2) not null,
    paid_amount numeric(12,2) not null,
    outstanding_amount numeric(12,2) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create index idx_orders_owner_date on orders(owner_id, order_date);

create table order_items (
    id bigserial primary key,
    order_id bigint not null references orders(id) on delete cascade,
    product_name varchar(100) not null,
    qty numeric(12,3),
    weight numeric(12,3),
    price numeric(12,2),
    amount numeric(12,2)
);

create table receivable_writeoffs (
    id bigserial primary key,
    order_id bigint not null references orders(id) on delete cascade,
    writeoff_date date not null,
    amount numeric(12,2) not null,
    remark varchar(300),
    created_at timestamp not null default now()
);

create table workers (
    id bigserial primary key,
    name varchar(100) not null,
    role varchar(30) not null,
    level varchar(30),
    phone varchar(30),
    status varchar(20) not null,
    base_salary numeric(12,2),
    remark varchar(300)
);

create table attendance (
    id bigserial primary key,
    worker_id bigint not null references workers(id) on delete cascade,
    work_date date not null,
    status varchar(20) not null,
    work_hours numeric(6,2),
    remark varchar(300)
);

create index idx_attendance_date on attendance(work_date);

create table salary_rules (
    id bigserial primary key,
    role varchar(30) not null unique,
    rule_type varchar(20) not null,
    price_per_ton numeric(12,2),
    daily_salary numeric(12,2),
    hourly_salary numeric(12,2)
);

create table production_records (
    id bigserial primary key,
    worker_id bigint not null references workers(id) on delete cascade,
    order_id bigint references orders(id),
    work_date date not null,
    tonnage numeric(12,3) not null
);

create index idx_production_date on production_records(work_date);

create table salary_payments (
    id bigserial primary key,
    worker_id bigint not null references workers(id) on delete cascade,
    pay_date date not null,
    amount numeric(12,2) not null,
    remark varchar(300),
    created_at timestamp not null default now()
);

create table account_logs (
    id bigserial primary key,
    worker_id bigint not null references workers(id) on delete cascade,
    type varchar(30) not null,
    change_amount numeric(12,2) not null,
    balance numeric(12,2) not null,
    created_at timestamp not null default now()
);

create index idx_account_worker_time on account_logs(worker_id, created_at);
