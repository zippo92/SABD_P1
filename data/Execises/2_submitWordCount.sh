#!/bin/bash
rm output -rvf 2>&1
#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "WordCount" \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \
     target/handson-spark-1.0.jar  \
