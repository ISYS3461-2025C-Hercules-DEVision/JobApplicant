FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw \
 && chmod +x mvnw \
 && ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
# RUN ./mvnw -q -DskipTests package
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
