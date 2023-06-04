FROM openjdk:17
WORKDIR /app
COPY . /app
RUN mvn clean install
RUN cp /app/booking-tech-app/target/shameless-booking-tech.jar /app/shameless-booking-tech.jar
ENTRYPOINT ["java", "-jar", "/app/shameless-booking-tech.jar"]