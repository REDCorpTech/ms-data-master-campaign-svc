# Stage 1: Build Stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven project files
COPY pom.xml ./
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
