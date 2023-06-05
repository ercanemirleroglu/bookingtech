FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY . /app

ENV MAVEN_OPTS="-Xmx512m"

RUN mvn clean install

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
            libasound2 libxt6 libxtst6 \
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

RUN cp /app/booking-tech-app/target/*.jar /app/app.jar

#Yürütülebilir JAR dosyasını belirtin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]