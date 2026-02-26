alter table salary_rules add column if not exists role_id bigint;
update salary_rules sr
set role_id = r.id
from worker_roles r
where sr.role_id is null and sr.role = r.code;

alter table salary_rules drop column if exists role;
create index if not exists idx_salary_rules_role_id on salary_rules(role_id);
