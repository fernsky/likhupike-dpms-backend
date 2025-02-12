--changeset author:trilochan:1
--comment: Create family search function
--validCheckSum: ANY
CREATE
OR REPLACE FUNCTION search_families (
    p_ward_number INTEGER DEFAULT NULL,
    p_social_category VARCHAR DEFAULT NULL,
    p_has_electricity BOOLEAN DEFAULT NULL,
    p_has_toilet BOOLEAN DEFAULT NULL,
    p_has_water_treatment BOOLEAN DEFAULT NULL
) RETURNS TABLE (
    id VARCHAR,
    head_of_family VARCHAR,
    ward_number INTEGER,
    social_category VARCHAR,
    total_members INTEGER,
    monthly_income DECIMAL,
    land_area DECIMAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        f.id,
        f.head_of_family,
        f.ward_number,
        f.social_category,
        f.total_members,
        f.monthly_income,
        f.land_area
    FROM families f
    WHERE 
        (p_ward_number IS NULL OR f.ward_number = p_ward_number)
        AND (p_social_category IS NULL OR f.social_category = p_social_category)
        AND (p_has_electricity IS NULL OR f.has_electricity = p_has_electricity)
        AND (p_has_toilet IS NULL OR f.has_toilet = p_has_toilet)
        AND (p_has_water_treatment IS NULL OR f.has_water_treatment_system = p_has_water_treatment)
    ORDER BY f.ward_number, f.head_of_family;
END;
$$ LANGUAGE plpgsql;

--rollback DROP FUNCTION IF EXISTS search_families;