#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "Query2" --master "local" Queries/target/Queries-1.0-SNAPSHOT.jar csv \
