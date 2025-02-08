-- Create database if not exists
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dpms') THEN
        CREATE DATABASE dpms WITH OWNER dpms;
    END IF;
END $$;

-- Create extensions
\c dpms
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Set up permissions
ALTER ROLE dpms WITH PASSWORD 'dpmsSecurePass123!';
GRANT ALL PRIVILEGES ON DATABASE dpms TO dpms;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dpms;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dpms;

-- Create schema for versioning
CREATE SCHEMA IF NOT EXISTS audit;

-- Create audit trigger function
CREATE
OR REPLACE FUNCTION audit.create_audit_trigger () RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO audit.audit_log 
        SELECT NOW(), TG_TABLE_NAME, TG_OP, OLD.*;
        RETURN OLD;
    ELSE
        INSERT INTO audit.audit_log 
        SELECT NOW(), TG_TABLE_NAME, TG_OP, NEW.*;
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Create audit log table
CREATE TABLE IF NOT EXISTS
    audit.audit_log (
        audit_time TIMESTAMP WITH TIME ZONE NOT NULL,
        table_name TEXT NOT NULL,
        operation TEXT NOT NULL,
        record JSONB NOT NULL
    );