#!/bin/bash
mvn clean install
JAR_FILE="booking-tech-app/target/shameless-booking-tech.jar"
DESTINATION="target/app.jar"
cp $JAR_FILE $DESTINATION