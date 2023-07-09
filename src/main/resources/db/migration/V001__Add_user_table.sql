create table if not exists public.user
(
    id   bigserial PRIMARY KEY,
    name varchar not null,
    age  integer
);

insert into public.user (id, name, age)
values (1, 'admin', 0);