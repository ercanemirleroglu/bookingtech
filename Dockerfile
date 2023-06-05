FROM maven:3.8.4-openjdk-17-slim AS build

# Firefox sürümünü belirle
ARG FIREFOX_VERSION=113.0.2

# Firefox'un indirme URL'sini oluştur
ARG FIREFOX_URL=https://ftp.mozilla.org/pub/firefox/releases/${FIREFOX_VERSION}/linux-x86_64/en-US/firefox-${FIREFOX_VERSION}.tar.bz2

# Geckodriver sürümünü belirle
ARG GECKODRIVER_VERSION=0.33.0

# Firefox ve Geckodriver'ı kur
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates curl bzip2 libgtk-3-0 && rm -rf /var/lib/apt/lists/* \
 && curl -sSL -o /tmp/firefox.tar.bz2 ${FIREFOX_URL} \
 && tar -xjf /tmp/firefox.tar.bz2 -C /opt \
 && ln -s /opt/firefox/firefox /usr/bin/firefox \
 && rm /tmp/firefox.tar.bz2 \
 && curl -sSL -o /tmp/geckodriver.tar.gz https://github.com/mozilla/geckodriver/releases/download/v${GECKODRIVER_VERSION}/geckodriver-v${GECKODRIVER_VERSION}-linux64.tar.gz \
 && tar -xzf /tmp/geckodriver.tar.gz -C /usr/local/bin \
 && rm /tmp/geckodriver.tar.gz

# Çalışma dizinini ayarlayın
WORKDIR /app

# Proje dosyalarını kopyalayın
COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

# Projenizi derleyin ve jar dosyasını oluşturun
RUN mvn clean install

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

# Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
