# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17 AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# OpenJDK 17 taban görüntüsünü kullanın
FROM adoptopenjdk:17-jdk-hotspot

# Çalışma dizinini ayarlayın
WORKDIR /app

# JAR dosyasını kopyalayın
COPY --from=build /app/booking-tech-app/target/*.jar /app/app.jar

# Xvfb servisini başlatın
ENTRYPOINT ["java", "-jar", "/app/app.jar"]