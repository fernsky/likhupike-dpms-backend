--changeset author:trilochan:1
--comment: Create view for active users
--validCheckSum: ANY
CREATE OR REPLACE VIEW
    active_users AS
SELECT
    u.*,
    array_agg(r.name) as role_names
FROM
    users u
    LEFT JOIN user_roles ur ON u.id = ur.user_id
    LEFT JOIN roles r ON ur.role_id = r.id
WHERE
    u.is_approved = true
    AND u.is_deleted = false
GROUP BY
    u.id;

--rollback DROP VIEW IF EXISTS active_users;