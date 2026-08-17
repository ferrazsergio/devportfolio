create table educations (
    id uuid primary key,
    portfolio_id uuid not null,
    institution varchar(255) not null,
    course varchar(255) not null,
    degree varchar(255),
    start_date date not null,
    end_date date,
    description text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_educations_portfolio foreign key (portfolio_id) references portfolios (id)
);

create index ix_educations_portfolio_id on educations (portfolio_id);
