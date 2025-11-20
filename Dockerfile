# Build stage with caching optimization
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace/app

# Copy maven configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage with security hardening
FROM eclipse-temurin:17-jre-alpine AS runtime

LABEL maintainer="ACME Solutions Team" \
      version="1.0" \
      description="Backend service for ACME Solutions Workshop Management System"

# Install curl for healthchecks
RUN apk --update --no-cache add curl

# Create non-root user
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR file
COPY --from=builder --chown=appuser:appgroup /workspace/app/target/*.jar app.jar

# Use non-root user
USER appuser

# Security and performance optimizations
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Expose only necessary port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]