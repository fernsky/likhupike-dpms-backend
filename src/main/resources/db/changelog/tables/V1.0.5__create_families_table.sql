--changeset author:trilochan:1
--comment: Create families and family_photos tables
--validCheckSum: ANY
CREATE TABLE
    families (
        id VARCHAR(36) PRIMARY KEY,
        head_of_family VARCHAR(255) NOT NULL,
        ward_number INTEGER NOT NULL,
        social_category VARCHAR(50) NOT NULL,
        total_members INTEGER NOT NULL,
        primary_water_source VARCHAR(50) NOT NULL,
        has_water_treatment_system BOOLEAN DEFAULT FALSE,
        distance_to_water_source DOUBLE PRECISION,
        construction_type VARCHAR(50) NOT NULL,
        total_rooms INTEGER NOT NULL,
        has_electricity BOOLEAN DEFAULT FALSE,
        has_toilet BOOLEAN DEFAULT FALSE,
        has_kitchen_garden BOOLEAN DEFAULT FALSE,
        housing_additional_details VARCHAR(500),
        monthly_income DECIMAL(19, 2) NOT NULL,
        has_employed_members BOOLEAN DEFAULT FALSE,
        number_of_employed_members INTEGER DEFAULT 0,
        receives_social_security BOOLEAN DEFAULT FALSE,
        has_bank_account BOOLEAN DEFAULT FALSE,
        has_loans BOOLEAN DEFAULT FALSE,
        land_area DECIMAL(19, 2) NOT NULL,
        has_irrigation BOOLEAN DEFAULT FALSE,
        livestock_count INTEGER DEFAULT 0,
        poultry_count INTEGER DEFAULT 0,
        has_greenhouse BOOLEAN DEFAULT FALSE,
        has_agricultural_equipment BOOLEAN DEFAULT FALSE,
        latitude DOUBLE PRECISION,
        longitude DOUBLE PRECISION,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP,
        created_by VARCHAR(36),
        updated_by VARCHAR(36),
        version INTEGER DEFAULT 0
    );

CREATE TABLE
    family_photos (
        id VARCHAR(36) PRIMARY KEY,
        family_id VARCHAR(36) NOT NULL,
        file_name VARCHAR(255) NOT NULL,
        content_type VARCHAR(100) NOT NULL,
        file_size BIGINT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        created_by VARCHAR(36),
        CONSTRAINT fk_family_photos_family FOREIGN KEY (family_id) REFERENCES families (id)
    );

CREATE TRIGGER families_audit_trigger BEFORE
UPDATE ON families FOR EACH ROW
EXECUTE FUNCTION update_audit_fields ();

--rollback DROP TRIGGER IF EXISTS families_audit_trigger ON families;
--rollback DROP TABLE IF EXISTS family_photos;
--rollback DROP TABLE IF EXISTS families;