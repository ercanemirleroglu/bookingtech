FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY . /app

RUN mvn clean install

# ChromeDriver sürümünü belirle
ARG CHROME_DRIVER_VERSION=113.0.5672.63

# ChromeDriver'ın indirme URL'sini oluştur
ARG CHROME_DRIVER_URL=https://chromedriver.storage.googleapis.com/${CHROME_DRIVER_VERSION}/chromedriver_linux64.zip

# ChromeDriver'ı indir ve kur
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl unzip \
 && rm -rf /var/lib/apt/lists/* \
 && curl -sSL -o /tmp/chromedriver.zip ${CHROME_DRIVER_URL} \
 && unzip /tmp/chromedriver.zip -d /usr/local/bin \
 && rm /tmp/chromedriver.zip

# Chrome tarayıcısını indir ve kur
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget gnupg ca-certificates procps \
 && rm -rf /var/lib/apt/lists/* \
 && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
 && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update && apt-get install -y google-chrome-stable \
 && rm -rf /var/lib/apt/lists/*

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
