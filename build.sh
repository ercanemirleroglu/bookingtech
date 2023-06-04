#!/bin/bash
export PATH="/opt/homebrew/bin:$PATH"
mvn clean install
JAR_FILE="booking-tech-app/target/shameless-booking-tech.jar"
DESTINATION="target/."
mkdir target
cp $JAR_FILE $DESTINATION