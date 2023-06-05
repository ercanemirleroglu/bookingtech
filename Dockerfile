# Docker taban görüntüsünü belirtin
FROM maven:3.8.4-openjdk-17-slim AS build

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

# Firefox yükleyin
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
    ca-certificates curl firefox-esr \
 && rm -fr /var/lib/apt/lists/*

# GeckoDriver'ı indirme ve kurma
RUN curl -sSL -o /tmp/geckodriver.tar.gz https://github.com/mozilla/geckodriver/releases/download/v0.32.0/geckodriver-v0.32.0-linux64.tar.gz \
 && tar -xzf /tmp/geckodriver.tar.gz -C /usr/local/bin \
 && rm /tmp/geckodriver.tar.gz

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
