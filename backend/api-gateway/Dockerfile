FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first so dependency download is cached
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
