# Ward Validation Rules

## Geographic Data Validation

- Latitude: Must be between -90° and +90°
- Longitude: Must be between -180° and +180°
- Coordinates precision: 6 decimal places
- Area: Must be positive, max 8 digits, 2 decimal places

## Ward Number Validation

- Must be between 1 and 33 (Nepal's ward limit)
- Must be unique within a municipality
- Cannot be changed after creation

## Text Field Validation

- Office Location: Max 100 characters
- Office Location (Nepali): Max 100 characters, UTF-8 encoding
- All text fields must be properly sanitized

## Relationship Validation

- Municipality reference must exist
- Municipality must be active

## Status Validation

- Cannot deactivate ward with active families
- Must track modification history
