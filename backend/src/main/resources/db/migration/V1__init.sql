-- Baseline migration for DevPortfolio.
-- Intentionally introduces no domain schema: the first real tables (users, portfolios)
-- are created in the Identity module migrations (Fase 2, V2__create_users.sql onward).
-- This file exists so the Flyway pipeline (and docker compose up) is exercised end-to-end
-- from the Foundation phase, per ADR-004 (Flyway as the single source of truth for schema).
select 1;
