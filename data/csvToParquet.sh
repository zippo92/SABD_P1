#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "AvroRead" --master "local"  --packages com.databricks:spark-avro_2.10:2.0.1 Queries/target/Queries-1.0-SNAPSHOT.jar  \
