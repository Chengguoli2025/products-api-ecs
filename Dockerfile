# Build stage
FROM --platform=linux/amd64 eclipse-temurin:17 AS build
WORKDIR /app

# Copy gradle files first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Set Gradle options to use local cache and avoid downloading
ENV GRADLE_OPTS="-Dgradle.user.home=/app/.gradle"

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8090

# Run the application with environment-specific profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILE:dev}"]
#ENTRYPOINT ["bash", "-c", "echo 'Testing DB connection...' && timeout 5 bash -c 'echo > /dev/tcp/database-1.cx0qnfjtc2ft.ap-southeast-2.rds.amazonaws.com/5432' && echo 'DB test passed, starting app...' || echo 'DB connection failed but continuing...' && java -jar app.jar --spring.profiles.active=${SPRING_PROFILE:-dev}"]
