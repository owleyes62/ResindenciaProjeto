# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw -B -DskipTests clean package \
    && mv target/*.jar target/app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /workspace/target/app.jar /app/app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
