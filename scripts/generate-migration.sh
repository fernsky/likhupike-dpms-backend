#!/bin/bash

# Script to generate database migrations from JPA entities

# Ensure the directories exist
mkdir -p src/main/resources/db/changelog/generated
mkdir -p src/main/resources/db/changelog/updates

# Generate the new schema from current entities
./gradlew generateSchema

# Get current timestamp for filename
TIMESTAMP=$(date +%Y%m%d%H%M%S)

# Copy the generated schema to updates directory with timestamp
cp src/main/resources/db/changelog/generated/schema.sql \
   src/main/resources/db/changelog/updates/${TIMESTAMP}-schema-update.sql

# Create a Liquibase changelog for this update
cat > src/main/resources/db/changelog/updates/${TIMESTAMP}-update.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <changeSet id="${TIMESTAMP}-schema-update" author="dpms-auto">
        <sqlFile path="db/changelog/updates/${TIMESTAMP}-schema-update.sql"/>
    </changeSet>
</databaseChangeLog>
EOF

echo "Migration files generated with timestamp: ${TIMESTAMP}"
