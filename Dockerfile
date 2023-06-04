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

# ChromeDriver'ı indirin ve doğru konuma yerleştirin
RUN apt-get install -y wget unzip \
    && wget -q https://chromedriver.storage.googleapis.com/113.0.5672.63/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip \
    && rm chromedriver_linux64.zip \
    && mv chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver

# Google Chrome'u yükleyin
RUN apt-get install -y curl \
    && curl -sS -o - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable=113.0.5672.126-1

# Xvfb ve Java için gerekli paketleri yükleyin
RUN apt-get install -y xvfb openjdk-17-jdk

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Xvfb servisini başlatın
CMD ["sh", "-c", "Xvfb :99 -screen 0 1024x768x24 -ac +extension RANDR & sleep 5 && DISPLAY=:99 java -jar /app/app.jar"]
