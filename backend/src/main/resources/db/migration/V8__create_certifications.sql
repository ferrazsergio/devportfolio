create table certifications (
    id uuid primary key,
    portfolio_id uuid not null,
    name varchar(255) not null,
    issuing_organization varchar(255) not null,
    issue_date date not null,
    expiration_date date,
    credential_url varchar(500),
    credential_id varchar(255),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_certifications_portfolio foreign key (portfolio_id) references portfolios (id)
);

create index ix_certifications_portfolio_id on certifications (portfolio_id);
