FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY . /app

RUN mvn clean install

FROM selenium/standalone-firefox:4.10.0-20230607

COPY --from=build /app/booking-tech-app/target/*.jar /app/app.jar

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]