# Build stage
FROM --platform=linux/amd64 eclipse-temurin:17 AS build
WORKDIR /app

# Pre-download Gradle distribution to avoid downloading during CI
RUN mkdir -p /root/.gradle/wrapper/dists/gradle-8.14.3-bin/
RUN curl -L https://services.gradle.org/distributions/gradle-8.14.3-bin.zip -o /tmp/gradle-8.14.3-bin.zip
RUN mkdir -p /root/.gradle/wrapper/dists/gradle-8.14.3-bin/abcdefghijklmnopqrstuvwxyz
RUN unzip /tmp/gradle-8.14.3-bin.zip -d /root/.gradle/wrapper/dists/gradle-8.14.3-bin/abcdefghijklmnopqrstuvwxyz
RUN rm /tmp/gradle-8.14.3-bin.zip

# Copy gradle files first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

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