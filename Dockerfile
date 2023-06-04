# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17 AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# Chromium paketini yükleyin
RUN apt-get update && apt-get install -y chromium-browser

# JAR dosyasını kopyalayın
RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Xvfb servisini başlatın
CMD ["sh", "-c", "Xvfb :99 -screen 0 1024x768x24 -ac +extension RANDR & sleep 5 && DISPLAY=:99 java -jar /app/app.jar"]

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]