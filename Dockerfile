FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

RUN mvn clean install

#Firefox sürümünü belirle
ARG FIREFOX_VERSION=113.0.2

#Firefox'un indirme URL'sini oluştur
ARG FIREFOX_URL=https://ftp.mozilla.org/pub/firefox/releases/${FIREFOX_VERSION}/linux-x86_64/en-US/firefox-${FIREFOX_VERSION}.tar.bz2

# Firefox'u indir ve kur
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates curl bzip2 libcairo2 libcairo-gobject2 libxt6 libsm6 libice6 libgtk-3-0 libx11-xcb1 libdbus-glib-1-2 psmisc xvfb libappindicator1 libasound2 libatk1.0-0 libatk-bridge2.0-0 libcairo-gobject2 libgconf-2-4 libgtk-3-0 libice6 libnspr4 libnss3 libsm6 libx11-xcb1 libxcomposite1 libxcursor1 libxdamage1 libxfixes3 libxi6 libxinerama1 libxrandr2 libxss1 libxt6 libxtst6 fonts-liberation && rm -rf /var/lib/apt/lists/* \
 && curl -sSL -o /tmp/firefox.tar.bz2 ${FIREFOX_URL} \
 && tar -xjf /tmp/firefox.tar.bz2 -C /opt \
 && ln -s /opt/firefox/firefox /usr/bin/firefox \
 && rm /tmp/firefox.tar.bz2

#Geckodriver sürümünü belirle
ARG GECKODRIVER_VERSION=0.30.0

# Geckodriver'ı indir ve kur
RUN curl -sSL -o /tmp/geckodriver.tar.gz https://github.com/mozilla/geckodriver/releases/download/v${GECKODRIVER_VERSION}/geckodriver-v${GECKODRIVER_VERSION}-linux64.tar.gz \
 && tar -xzf /tmp/geckodriver.tar.gz -C /usr/local/bin \
 && rm /tmp/geckodriver.tar.gz

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

#Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]