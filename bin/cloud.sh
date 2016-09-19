#!/usr/bin/env bash

BASEDIR=$(dirname "$0")

JAVA_OPTS="$JAVA_OPTS -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"

JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"

java $JAVA_OPTS -jar $BASEDIR/../build/libs/cloud-all.jar $@