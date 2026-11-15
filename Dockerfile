FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/kinemo-backend-0.0.1-SNAPSHOT.jar /app/kinemo.jar
ENV SPRING_PROFILES_ACTIVE=public-demo
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+UseSerialGC"
EXPOSE 10000
USER 10001
ENTRYPOINT ["java", "-jar", "/app/kinemo.jar"]
