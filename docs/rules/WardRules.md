# Ward Business Rules

## Administrative Rules

### Creation Rules

1. Only Municipality Admins can create wards
2. Ward number must be unique within municipality
3. Must provide bilingual information
4. Must specify geographic coordinates

### Update Rules

1. Cannot change ward number once created
2. Cannot change municipality assignment
3. Must maintain audit trail
4. Must validate geographic boundaries

### Deletion Rules

1. Soft delete only
2. Must check for dependent records
3. Must archive historical data
4. Must maintain deletion audit trail

## Access Control Rules

1. Municipality admins have full access
2. Ward admins can only access their ward
3. Viewers have read-only access
4. Special permissions for statistical data

## Data Integrity Rules

1. Maintain historical records
2. Ensure data consistency
3. Handle bilingual content properly
4. Validate geographic data accuracy
