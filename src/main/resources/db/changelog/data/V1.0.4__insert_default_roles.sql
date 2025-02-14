--changeset author:trilochan:1
--comment: Insert default roles
--validCheckSum: ANY
INSERT INTO
    roles (id, role_type, description)
VALUES
    (
        '1',
        'SUPER_ADMIN',
        'Super Administrator with full system access'
    ),
    (
        '2',
        'MUNICIPALITY_ADMIN',
        'Municipality level administrator'
    ),
    ('3', 'WARD_ADMIN', 'Ward level administrator'),
    ('4', 'EDITOR', 'Can edit and manage content'),
    ('5', 'VIEWER', 'Read-only access to content') ON CONFLICT (id)
DO NOTHING;

--rollback DELETE FROM roles WHERE name IN ('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR', 'VIEWER');