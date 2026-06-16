# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

# Copy the POMs first so dependency resolution is cached when only sources change.
COPY pom.xml .
COPY domain/pom.xml domain/
COPY infrastructure/pom.xml infrastructure/
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

# Copy the sources and build the bootable jar (the infrastructure module is the app).
COPY domain/src domain/src
COPY infrastructure/src infrastructure/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl infrastructure -am clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Run as an unprivileged user.
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /build/infrastructure/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
