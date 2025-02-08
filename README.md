# DPMS API

## Register User

To register a user, use the following curl request:

```sh
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "password": "Password@123",
  "fullName": "John Doe",
  "fullNameNepali": "जोन डो",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St",
  "officePost": "Manager",
  "wardNumber": 5
}'
```
