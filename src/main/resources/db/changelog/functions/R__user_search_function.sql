--changeset author:trilochan:1
--comment: Create user search function
--validCheckSum: ANY
CREATE
OR REPLACE FUNCTION search_users (
    p_ward_number integer,
    p_is_municipality_level boolean,
    p_search_term text,
    p_is_approved boolean
) RETURNS TABLE (
    id varchar,
    email varchar,
    full_name varchar,
    ward_number integer,
    office_post varchar,
    is_approved boolean,
    roles text[]
) AS $function$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        u.email,
        u.full_name,
        u.ward_number,
        u.office_post,
        u.is_approved,
        array_agg(DISTINCT r.name) as roles
    FROM users u
    LEFT JOIN user_roles ur ON u.id = ur.user_id
    LEFT JOIN roles r ON ur.role_id = r.id
    WHERE u.is_deleted = false
    AND (p_ward_number IS NULL OR u.ward_number = p_ward_number)
    AND (p_is_municipality_level IS NULL OR u.is_municipality_level = p_is_municipality_level)
    AND (p_is_approved IS NULL OR u.is_approved = p_is_approved)
    AND (
        p_search_term IS NULL OR
        u.full_name ILIKE '%' || p_search_term || '%' OR
        u.email ILIKE '%' || p_search_term || '%' OR
        u.office_post ILIKE '%' || p_search_term || '%'
    )
    GROUP BY u.id, u.email, u.full_name, u.ward_number, u.office_post, u.is_approved;
END;
$function$ LANGUAGE plpgsql;

--rollback DROP FUNCTION IF EXISTS search_users(integer, boolean, text, boolean);