#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "Query1" \
    --master local \
    --deploy-mode client \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \
     Queries/target/Queries-1.0-SNAPSHOT.jar  \
