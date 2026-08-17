create table skills (
    id uuid primary key,
    portfolio_id uuid not null,
    name varchar(100) not null,
    category varchar(20) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_skills_portfolio foreign key (portfolio_id) references portfolios (id),
    constraint ck_skills_category check (category in ('BACKEND', 'FRONTEND', 'DATABASE', 'CLOUD', 'DEVOPS', 'TOOLS', 'OTHER'))
);

-- Índice único case-insensitive: alinhado com a checagem de duplicidade da aplicação
-- (existsByPortfolioIdAndNameIgnoreCase), não apenas uma constraint case-sensitive.
create unique index uk_skills_portfolio_name on skills (portfolio_id, lower(name));
