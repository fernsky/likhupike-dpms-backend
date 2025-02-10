--changeset author:trilochan:1
--comment: Create roles table
--validCheckSum: ANY
CREATE TABLE
    roles (
        id VARCHAR(36) PRIMARY KEY,
        name VARCHAR(50) NOT NULL UNIQUE,
        description VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP,
        created_by VARCHAR(36),
        updated_by VARCHAR(36),
        version INT DEFAULT 0
    );

CREATE TRIGGER roles_audit_trigger BEFORE
UPDATE ON roles FOR EACH ROW
EXECUTE FUNCTION update_audit_fields ();

--rollback DROP TRIGGER IF EXISTS roles_audit_trigger ON roles;
--rollback DROP TABLE IF EXISTS roles;