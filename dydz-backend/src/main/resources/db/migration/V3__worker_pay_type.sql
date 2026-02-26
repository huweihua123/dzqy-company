alter table workers add column if not exists pay_type varchar(20) not null default 'PIECE';
alter table workers add column if not exists yearly_salary numeric(12,2);
