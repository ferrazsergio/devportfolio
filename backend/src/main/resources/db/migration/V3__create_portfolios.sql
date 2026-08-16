create table portfolios (
    id uuid primary key,
    owner_user_id uuid not null,
    status varchar(20) not null default 'DRAFT',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_portfolios_owner_user_id unique (owner_user_id),
    constraint fk_portfolios_owner_user foreign key (owner_user_id) references users (id),
    constraint ck_portfolios_status check (status in ('DRAFT', 'PUBLISHED'))
);
