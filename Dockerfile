# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17 AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# Selenium/standalone-chrome görüntüsünü kullanın
FROM selenium/standalone-chrome:latest

# Çalışma dizinini ayarlayın
WORKDIR /app

# JAR dosyasını kopyalayın
COPY --from=build /app/booking-tech-app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]