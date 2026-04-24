# Stage 1: Build the application using a Maven image
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the pom.xml and download dependencies to cache them
# This helps Docker cache the downloaded dependencies layer, speeding up subsequent builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application, skipping tests (tests should ideally be run in the CI/CD pipeline)
RUN mvn clean package -DskipTests

# Stage 2: Create the production image with a minimal JRE
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root user to run the application for security purposes
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the builder stage to the final image
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port (Spring Boot default is 8080)
EXPOSE 8080

# Set timezone if needed (uncomment and adjust if your app is time-sensitive)
# ENV TZ=Asia/Ho_Chi_Minh

# Run the jar file with basic JVM optimizations for containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]