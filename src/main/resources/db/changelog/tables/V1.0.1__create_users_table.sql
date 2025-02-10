--changeset author:trilochan:1
--comment: Create users table with all necessary columns
--validCheckSum: ANY
--rollback DROP TABLE users;
CREATE TABLE
    users (
        id varchar(36) PRIMARY KEY,
        email varchar(255) NOT NULL UNIQUE,
        password varchar(255) NOT NULL,
        full_name varchar(255) NOT NULL,
        full_name_np varchar(255) NOT NULL,
        date_of_birth date,
        address varchar(500),
        profile_picture varchar(255),
        office_post varchar(255),
        ward_number integer,
        is_municipality_level boolean DEFAULT false,
        is_approved boolean DEFAULT false,
        approved_by varchar(36),
        approved_at timestamp,
        is_deleted boolean DEFAULT false,
        deleted_at timestamp,
        deleted_by varchar(36),
        created_at timestamp DEFAULT CURRENT_TIMESTAMP,
        updated_at timestamp,
        created_by varchar(36),
        updated_by varchar(36),
        version integer DEFAULT 0,
        CONSTRAINT users_approved_by_fk FOREIGN KEY (approved_by) REFERENCES users (id),
        CONSTRAINT users_deleted_by_fk FOREIGN KEY (deleted_by) REFERENCES users (id)
    );

CREATE INDEX idx_users_email ON users (email)
WHERE
    is_deleted = false;

CREATE INDEX idx_users_ward_number ON users (ward_number)
WHERE
    is_deleted = false;

CREATE INDEX idx_users_approval_status ON users (is_approved, ward_number)
WHERE
    is_deleted = false;

CREATE
OR REPLACE FUNCTION update_audit_fields () RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_audit_trigger BEFORE
UPDATE ON users FOR EACH ROW
EXECUTE FUNCTION update_audit_fields ();

--rollback script for emergency
--rollback DROP TRIGGER IF EXISTS users_audit_trigger ON users;
--rollback DROP FUNCTION IF EXISTS update_audit_fields();
--rollback DROP INDEX IF EXISTS idx_users_email;
--rollback DROP INDEX IF EXISTS idx_users_ward_number;
--rollback DROP INDEX IF EXISTS idx_users_approval_status;
--rollback DROP TABLE IF EXISTS users;
--changeset author:trilochan:2
--comment: Add version control for optimistic locking
ALTER TABLE users
ADD COLUMN IF NOT EXISTS version integer DEFAULT 0;

--changeset author:trilochan:3
--comment: Add check constraints
ALTER TABLE users
ADD CONSTRAINT chk_email_format CHECK (
    email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
);