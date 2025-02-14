# Build stage
FROM gradle:8.6.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test -Pprofile=prod

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set production profile
ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-jar", "app.jar"]
