# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17 AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# ChromeDriver'ı indirin ve doğru konuma yerleştirin
RUN apt-get install -y wget unzip \
    && wget -q https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip \
    && rm chromedriver_linux64.zip \
    && mv chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver

# JAR dosyasını kopyalayın
RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Xvfb servisini başlatın
CMD ["sh", "-c", "Xvfb :99 -screen 0 1024x768x24 -ac +extension RANDR & sleep 5 && DISPLAY=:99"]

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]