# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17 AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# Selenium/standalone-chrome görüntüsünü kullanın
FROM selenium/standalone-chrome

# Çalışma dizinini ayarlayın
WORKDIR /app

# JAR dosyasını kopyalayın
COPY --from=build /app/booking-tech-app/target/*.jar /app/app.jar

# Xvfb servisini başlatın
CMD ["sh", "-c", "Xvfb :99 -screen 0 1024x768x24 -ac +extension RANDR & sleep 5 && DISPLAY=:99 java -jar /app/app.jar"]
