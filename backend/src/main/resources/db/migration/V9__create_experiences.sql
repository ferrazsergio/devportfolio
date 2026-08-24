create table experiences (
    id uuid primary key,
    portfolio_id uuid not null,
    company varchar(255) not null,
    role varchar(255) not null,
    description text,
    start_date date not null,
    end_date date,
    current boolean not null default false,
    location varchar(255),
    display_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_experiences_portfolio foreign key (portfolio_id) references portfolios (id)
);

create index ix_experiences_portfolio_id on experiences (portfolio_id);

create table experience_technology (
    experience_id uuid not null,
    skill_id uuid not null,
    constraint pk_experience_technology primary key (experience_id, skill_id),
    constraint fk_experience_technology_experience foreign key (experience_id) references experiences (id),
    constraint fk_experience_technology_skill foreign key (skill_id) references skills (id)
);
