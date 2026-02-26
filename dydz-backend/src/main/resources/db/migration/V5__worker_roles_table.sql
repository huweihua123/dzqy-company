create table if not exists worker_roles (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(100) not null
);

insert into worker_roles (code, name) values
    ('MASTER', '大师傅'),
    ('SECOND_MASTER', '二师傅'),
    ('OPERATOR', '操作机工人'),
    ('HAMMER', '司锤工'),
    ('FORGING_HELPER', '锻造小工'),
    ('SAW', '锯料工'),
    ('LATHE', '车工')
on conflict (code) do nothing;

alter table workers add column if not exists role_id bigint;
update workers w
set role_id = r.id
from worker_roles r
where w.role_id is null and w.role = r.code;

alter table workers drop column if exists role;

create index if not exists idx_workers_role_id on workers(role_id);
