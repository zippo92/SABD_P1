#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "Query2" --master "local" --packages com.databricks:spark-avro_2.10:1.0.0 Queries/target/Queries-1.0-SNAPSHOT.jar avro local \
