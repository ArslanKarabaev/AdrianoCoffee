#FROM eclipse-temurin:17-jre-alpine
#ARG JAR_FILE=target/*.jar
#COPY ./target/AdrianoCoffee-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/target/AdrianoCoffee-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT:-8080}", "-jar", "/app.jar"]