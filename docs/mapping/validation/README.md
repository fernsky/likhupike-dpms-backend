# Validation Rules Documentation

## Overview

This section defines validation rules for all data transformations.

## Common Validation Rules

### Geographic Data

- Latitude: -90 to 90 degrees
- Longitude: -180 to 180 degrees
- Coordinates precision: 6 decimal places
- Area: Must be positive, max 8 digits, 2 decimal places

### Text Fields

- Names: Max 100 characters
- Codes: Uppercase alphanumeric, max 10 characters
- Descriptions: Max 500 characters
- URLs: Valid URL format, max 255 characters

### Numeric Data

- Population: Non-negative integer
- Counts: Non-negative integer
- Percentages: 0-100, 2 decimal places
- Currency: Non-negative, 2 decimal places

### Date/Time

- Dates: ISO-8601 format
- Timestamps: UTC timezone
- Future dates not allowed for creation/birth dates
- Past dates within reasonable range

### Identifiers

- UUID format validation
- Reference integrity checks
- Uniqueness constraints
