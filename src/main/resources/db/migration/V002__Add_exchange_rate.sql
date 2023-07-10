create table if not exists public.exchange_rate
(
    date date             not null,
    rate double precision not null
);

comment on column public.exchange_rate.date is '날짜';

comment on column public.exchange_rate.rate is '달러 -> 원화 환율';