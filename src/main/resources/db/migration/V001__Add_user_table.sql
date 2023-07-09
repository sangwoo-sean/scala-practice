create table user
(
    id   bigserial PRIMARY KEY,
    name varchar not null,
    age  integer
);

insert into user (id, name, age)
values 1, 'admin', 0;