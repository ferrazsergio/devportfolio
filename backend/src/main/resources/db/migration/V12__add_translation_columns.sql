-- Colunas de tradução automática (EN) para os campos de prosa livre do conteúdo do
-- usuário — ver ADR-006 (i18n como camada adicional) e docs de i18n. Todas nullable:
-- linhas existentes ficam sem tradução até o próximo salvamento (degrada bem — a
-- página pública em inglês cai para o texto original se não houver tradução ainda).

alter table profiles add column headline_en varchar(255);
alter table profiles add column bio_en text;

alter table experiences add column description_en text;

alter table projects add column short_description_en varchar(500);
alter table projects add column full_description_en text;

alter table educations add column description_en text;
