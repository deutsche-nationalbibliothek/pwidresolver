FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src

COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM docker.io/library/eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /src/target/*-SNAPSHOT.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
