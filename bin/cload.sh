#!/usr/bin/bash -x

BASEDIR=$(dirname "$0")

mkdir $BASEDIR/../logs
JAVA_OPTS="$JAVA_OPTS -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=2M"
JAVA_OPTS="$JAVA_OPTS -Xloggc:$BASEDIR/../logs/`date +%F_%H-%M-%S`-gc.log"

java $JAVA_OPTS -jar $BASEDIR/../build/libs/cload-all.jar $@
