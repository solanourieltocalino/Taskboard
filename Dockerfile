# ---------- Stage 1: Build with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build

# Working directory within the container
WORKDIR /app

# Copy the pom first to leverage dependency cache
COPY pom.xml .

# Download dependencies (without compiling yet)
RUN mvn -q -B dependency:go-offline

# Copy the source code
COPY src ./src

# Compile and package the jar (skip tests to make image build faster)
RUN mvn -q -B clean package -DskipTests


# ---------- Stage 2: Lightweight runtime image ----------
FROM eclipse-temurin:17-jre-alpine

# Working directory within the final image
WORKDIR /app

# Copy the jar from the build stage
# Adjust the pattern if your JAR has a different name, but usually *.jar works
COPY --from=build /app/target/*.jar app.jar

# Port exposed by the app (Spring Boot uses 8080 by default)
EXPOSE 8080

# Minimal environment variables (DB ones will be set with docker-compose)
ENV SPRING_PROFILES_ACTIVE=dev

# Startup command
ENTRYPOINT ["java", "-jar", "app.jar"]
