#FROM eclipse-temurin:17-jre-alpine
#ARG JAR_FILE=target/*.jar
#COPY ./target/AdrianoCoffee-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:17-jre-alpine
COPY ./target/AdrianoCoffee-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]