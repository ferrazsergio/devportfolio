create table github_connections (
    id uuid primary key,
    portfolio_id uuid not null,
    github_username varchar(255) not null,
    encrypted_access_token text not null,
    connected_at timestamptz not null default now(),
    constraint uk_github_connections_portfolio_id unique (portfolio_id),
    constraint fk_github_connections_portfolio foreign key (portfolio_id) references portfolios (id)
);
