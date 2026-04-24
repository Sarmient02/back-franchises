# Stage 1: Build the application using JDK
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build configuration files
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY main.gradle .
COPY settings.gradle .
COPY gradle.properties* .

# Copy source code
COPY applications/ applications/
COPY domain/ domain/
COPY infrastructure/ infrastructure/

# Build the JAR (skip tests and structure validation)
RUN chmod +x gradlew && ./gradlew :app-service:bootJar -x test -x validateStructure --no-daemon

# Stage 2: Run the application using JRE only (lighter image)
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from the builder stage
COPY --from=builder /app/applications/app-service/build/libs/Franchises.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
