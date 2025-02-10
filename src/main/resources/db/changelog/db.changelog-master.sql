-- Master changelog file that includes all other changelogs
-- Naming convention: V{major}.{minor}.{patch}__{description}.sql
--changeset author:trilochan:1
--comment: Initial schema setup
CREATE SCHEMA IF NOT EXISTS dpms;

--includeAll path=schemas/
--includeAll path=tables/
--includeAll path=data/
--includeAll path=views/
--includeAll path=functions/