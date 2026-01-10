# =========================
# 1) Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn -q -DskipTests clean package


# =========================
# 2) Run stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose your service port
EXPOSE 8084

# Optional: good JVM defaults for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
