create table projects (
    id uuid primary key,
    portfolio_id uuid not null,
    name varchar(255) not null,
    slug varchar(100) not null,
    short_description varchar(500),
    full_description text,
    image_url varchar(500),
    github_url varchar(500),
    demo_url varchar(500),
    date date,
    status varchar(20) not null,
    featured boolean not null default false,
    display_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_projects_portfolio foreign key (portfolio_id) references portfolios (id),
    constraint ck_projects_status check (status in ('IN_PROGRESS', 'COMPLETED', 'ARCHIVED'))
);

create index ix_projects_portfolio_id on projects (portfolio_id);

-- case-insensitive, alinhado com a checagem existsByPortfolioIdAndSlugIgnoreCase da aplicação
create unique index uk_projects_portfolio_slug on projects (portfolio_id, lower(slug));

create table project_technology (
    project_id uuid not null,
    skill_id uuid not null,
    constraint pk_project_technology primary key (project_id, skill_id),
    constraint fk_project_technology_project foreign key (project_id) references projects (id),
    constraint fk_project_technology_skill foreign key (skill_id) references skills (id)
);
