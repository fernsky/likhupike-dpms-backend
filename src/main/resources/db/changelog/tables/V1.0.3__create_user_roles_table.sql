--changeset author:trilochan:1
--comment: Create user_roles junction table
--validCheckSum: ANY
CREATE TABLE
    user_roles (
        user_id VARCHAR(36) NOT NULL,
        role_id VARCHAR(36) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (user_id, role_id),
        CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
        CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
    );

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);

CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

--rollback DROP INDEX IF EXISTS idx_user_roles_role_id;
--rollback DROP INDEX IF EXISTS idx_user_roles_user_id;
--rollback DROP TABLE IF EXISTS user_roles;