FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /build
COPY rental-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY rental-service/src src
RUN mvn package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build /build/target/rental-service-0.0.1-SNAPSHOT.jar /app/rental-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar","rental-service-0.0.1-SNAPSHOT.jar"]