# Docker taban görüntüsünü belirtin
FROM selenium/standalone-chrome
FROM maven:3.8.4-openjdk-17-slim AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# JAR dosyasını çalışma dizinine kopyalayın
RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
