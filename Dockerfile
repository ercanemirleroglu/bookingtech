FROM debian:bullseye-slim

ARG firefox_ver=113.0.2
ARG geckodriver_ver=0.33.0

RUN apt-get update \
 && apt-get upgrade -y \
 && apt-get install -y --no-install-recommends --no-install-suggests \
            ca-certificates \
 && update-ca-certificates \
    \
 # Install tools for building
 && toolDeps=" \
        curl bzip2 \
    " \
 && apt-get install -y --no-install-recommends --no-install-suggests \
            $toolDeps \
    \
 # Install dependencies for Firefox
 && apt-get install -y --no-install-recommends --no-install-suggests \
            `apt-cache depends firefox-esr | awk '/Depends:/{print$2}'` \
            # additional 'firefox-esl' dependencies which is not in 'depends' list
            libcairo2 libcairo-gobject2 libxt6 libsm6 libice6 libgtk-3-0 libx11-xcb1 libdbus-glib-1-2 psmisc xvfb libappindicator1 libasound2 libatk1.0-0 libatk-bridge2.0-0 libcairo-gobject2 libgconf-2-4 libgtk-3-0 libice6 libnspr4 libnss3 libsm6 libx11-xcb1 libxcomposite1 libxcursor1 libxdamage1 libxfixes3 libxi6 libxinerama1 libxrandr2 libxss1 libxt6 libxtst6 fonts-liberation \
    \
 # Download and install Firefox
 && curl -fL -o /tmp/firefox.tar.bz2 \
         https://ftp.mozilla.org/pub/firefox/releases/${firefox_ver}/linux-x86_64/en-GB/firefox-${firefox_ver}.tar.bz2 \
 && tar -xjf /tmp/firefox.tar.bz2 -C /opt/ \
    \
 # Download and install geckodriver
 && curl -fL -o /tmp/geckodriver.tar.gz \
         https://github.com/mozilla/geckodriver/releases/download/v${geckodriver_ver}/geckodriver-v${geckodriver_ver}-linux64.tar.gz \
 && tar -xzf /tmp/geckodriver.tar.gz -C /usr/local/bin/ \
 && chmod +x /usr/local/bin/geckodriver \
    \
 # Cleanup unnecessary stuff
 && apt-get purge -y --auto-remove \
                  -o APT::AutoRemove::RecommendsImportant=false \
            $toolDeps \
 && rm -rf /var/lib/apt/lists/* \
           /tmp/*


# As this image cannot run in non-headless mode anyway, it's better to forcibly
# enable it, regardless whether WebDriver client requests it in capabilities or
# not.
ENV MOZ_HEADLESS=1

FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

RUN mvn clean install

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

#Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]