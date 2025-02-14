-- Municipality Admin User
INSERT INTO
    users (
        id,
        email,
        password, -- This is BCrypt encoded for 'Admin@123' - you should change this in production
        full_name,
        full_name_np,
        date_of_birth,
        address,
        office_post,
        is_municipality_level,
        is_approved,
        approved_at,
        created_at
    )
VALUES
    (
        '550e8400-e29b-41d4-a716-446655440000',
        'admin@likhupike.gov.np',
        '$2a$11$ypGBf.8UvxdXJBM2aY6wU./9qhKuFmiRpkTDfwSS7kWGLqKBYn9bS',
        'Municipality Admin',
        'नगरपालिका प्रशासक',
        '1980-01-01',
        'Likhu Pike Municipality',
        'Chief Administrative Officer',
        true,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ) ON CONFLICT (id)
DO NOTHING;

-- Role Assignments
INSERT INTO
    user_roles (user_id, role_id)
VALUES
    ('5b498777-939e-41aa-ac7f-aa7267e0dc3c', '2'),
    ('5b498777-939e-41aa-ac7f-aa7267e0dc3c', '4') ON CONFLICT (user_id, role_id)
DO NOTHING;

--rollback DELETE FROM user_roles WHERE user_id = '550e8400-e29b-41d4-a716-446655440000';
--rollback DELETE FROM users WHERE id = '550e8400-e29b-41d4-a716-446655440000';