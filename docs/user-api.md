# User Management API Documentation

## Overview

The User Management API provides a comprehensive set of endpoints for managing users within the municipality system. It supports role-based access control, advanced searching, and user lifecycle management.

## Authentication

All endpoints require Bearer token authentication:

## Endpoints

### Search Users

`GET /api/v1/users/search`

Search and filter users with multiple criteria.

#### Query Parameters

- `wardNumberFrom` (optional): Min ward number (1-32)
- `wardNumberTo` (optional): Max ward number (1-32)
- `searchTerm` (optional): Search in name, email (min 2 chars)
- `roles` (optional): Filter by roles (comma-separated)
- `officePosts` (optional): Filter by office posts (comma-separated)
- `createdAfter` (optional): ISO date (YYYY-MM-DD)
- `createdBefore` (optional): ISO date (YYYY-MM-DD)
- `dateOfBirthFrom` (optional): ISO date (YYYY-MM-DD)
- `dateOfBirthTo` (optional): ISO date (YYYY-MM-DD)
- `isApproved` (optional): true/false
- `isMunicipalityLevel` (optional): true/false
- `sortBy` (optional): CREATED_AT, FULL_NAME, EMAIL, WARD_NUMBER, OFFICE_POST, DATE_OF_BIRTH
- `sortDirection` (optional): ASC/DESC
- `page` (optional): Page number (0-based)
- `pageSize` (optional): Results per page (1-100)

## Search Parameters

### Sort Fields

The following sort fields are available:

- `CREATED_AT`: Sort by creation date (default)
- `FULL_NAME`: Sort by English name
- `FULL_NAME_NEPALI`: Sort by Nepali name
- `WARD_NUMBER`: Sort by ward number
- `OFFICE_POST`: Sort by office post
- `EMAIL`: Sort by email address
- `APPROVAL_STATUS`: Sort by approval status

### Example Requests

Sort by name ascending:
