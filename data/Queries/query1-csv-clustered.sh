#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "Query1" \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \
     target/Queries-1.0-SNAPSHOT.jar csv yarn \
