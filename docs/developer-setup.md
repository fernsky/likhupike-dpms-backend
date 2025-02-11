# Developer Setup Guide

## Prerequisites

- JDK 17 or later
- Gradle 8.x
- Docker (for running dependencies locally)
- Your favorite IDE (IntelliJ IDEA recommended)
- Git

## Project Structure

```
dpms-api/
├── src/
│   ├── main/
│   │   ├── kotlin/                 # Main source code
│   │   └── resources/              # Configuration files
│   └── test/
│       ├── kotlin/                 # Test source code
│       └── resources/              # Test configurations
├── build.gradle.kts                # Gradle build configuration
└── docker-compose.services.yml     # Local development dependencies
```

## Getting Started

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd dpms-api
   ```

2. Start local dependencies:

   ```bash
   docker-compose -f docker-compose.services.yml up -d
   ```

3. Build the project:

   ```bash
   ./gradlew build
   ```

4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## Development Workflow

### Building

- Build without tests: `./gradlew build -x test`
- Clean build: `./gradlew clean build`
- Generate API documentation: `./gradlew generateOpenApiDocs`

### Testing

The project follows a structured testing approach:

- Unit Tests: `src/test/kotlin/**/*Test.kt`
- Integration Tests: `src/test/kotlin/**/*IT.kt`
- API Tests: `src/test/kotlin/**/*ApiTest.kt`

Running tests:

```bash
# Run all tests
./gradlew test

# Run specific test category
./gradlew test --tests "*Test"      # Unit tests
./gradlew test --tests "*IT"        # Integration tests
./gradlew test --tests "*ApiTest"   # API tests
```

### Test Structure

```kotlin
@Test
fun `test description in backticks`() {
    // Given - setup

    // When - action

    // Then - verification
}
```

### Code Style

- Follow Kotlin coding conventions
- Use meaningful names
- Write comments for complex logic
- Each class should have a single responsibility
- Write unit tests for all business logic

## Configuration

### Application Properties

- `application.yml` - Main configuration
- `application-local.yml` - Local development settings
- `application-test.yml` - Test configuration

### Environment Variables

Required environment variables:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`

## Deployment

1. Build production JAR:

   ```bash
   ./gradlew bootJar
   ```

2. Run JAR:
   ```bash
   java -jar build/libs/dpms-api-{version}.jar
   ```

## Troubleshooting

Common issues and solutions:

1. Database connection issues:

   - Verify Docker containers are running
   - Check database credentials
   - Ensure database migrations are up to date

2. Build failures:
   - Run `./gradlew clean`
   - Update Gradle wrapper: `./gradlew wrapper --gradle-version=8.x`
   - Verify JDK version

## Contributing

1. Create a feature branch
2. Write tests
3. Implement changes
4. Run all tests
5. Submit pull request

## API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

## Support

Contact the development team for additional support:

- Email: dev@intensivestudy.com.np
