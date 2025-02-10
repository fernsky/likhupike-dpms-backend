--changeset author:trilochan:1
--comment: Create initial schema and extensions
--validCheckSum: ANY
CREATE SCHEMA IF NOT EXISTS dpms;

-- Enable necessary PostgreSQL extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE EXTENSION IF NOT EXISTS "btree_gist";

-- Create audit function for tracking changes
CREATE
OR REPLACE FUNCTION update_audit_fields () RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;