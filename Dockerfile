FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /workspace/app/target/web-crawler-1.0-SNAPSHOT.jar web-crawler-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "web-crawler-1.0-SNAPSHOT.jar"]
