# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17-slim AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# Chromium paketini yükleyin
RUN apt-get update && apt-get install -y chromium

# WebDriver'ı kopyalayın
#COPY chromedriver /usr/local/bin/

# JAR dosyasını çalışma dizinine kopyalayın
RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
