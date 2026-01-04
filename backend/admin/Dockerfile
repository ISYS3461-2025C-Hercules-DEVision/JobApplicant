FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1) copy pom trước để cache dependencies
COPY pom.xml ./

# 2) tải dependencies trước (cache layer)
RUN mvn -q -DskipTests dependency:go-offline

# 3) copy source sau
COPY src ./src

# 4) build jar
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]