#!/bin/bashlocal
$SPARK_HOME/bin/spark-submit --class "Query1" --master "local" --packages com.databricks:spark-avro_2.10:1.0.0 target/Queries-1.0-SNAPSHOT.jar avro local \
