#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "Utils.FilterWrongLines" --master "local" /data/Queries/target/Queries-1.0-SNAPSHOT.jar csv  \
