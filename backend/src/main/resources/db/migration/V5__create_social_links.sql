create table social_links (
    id uuid primary key,
    portfolio_id uuid not null,
    platform varchar(100) not null,
    url varchar(500) not null,
    display_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_social_links_portfolio foreign key (portfolio_id) references portfolios (id)
);

create index ix_social_links_portfolio_id on social_links (portfolio_id);
