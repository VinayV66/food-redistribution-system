# ============================================================
# Backend Dockerfile
# Multi-stage build:
#   Stage 1: Maven build (creates the JAR)
#   Stage 2: Lightweight runtime image
# ============================================================

# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy POM first (Docker cache optimization: only re-downloads deps when POM changes)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the JAR from the build stage
COPY --from=build /app/target/food-rescue-backend-*.jar app.jar

# Expose the Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
