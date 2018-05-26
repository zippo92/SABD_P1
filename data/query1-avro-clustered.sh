#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "Query1" \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \
    --packages com.databricks:spark-avro_2.10:1.0.0 Queries/target/Queries-1.0-SNAPSHOT.jar avro yarn \
