#!/bin/bash
rm output -rvf 2>&1
$SPARK_HOME/bin/spark-submit --class "WordCount" --master "yarn" target/handson-spark-1.0.jar 
