# Project Title

Progetto 1 - Sistemi e Architetture per Big data

### Quickstart

Run for build the container:

```
sudo docker build -t mysparkimage .
```

Then you can use this script for create 4 images, of which 1 master and 3 slaves


```
./create.sh
```
and this script for destroy them
```
./destroy.sh
```


once inside the master image you can use this scrpt for initialize all the environment (hdfs - yarn - spark - nifi - hbase)


```
./data/script/startAll.sh
```

## Inject the dataset

Go to

```
localhost:5555/nifi
```

and select all the processors (CTRL+A) and press the play button ▻
(there are 2 templates, one for data injection, and one is for saving the results on HBASe)


## Run the queries

The binaries and the sources of the query are located in /Queries

There are also 12 script for execute them in local/clustered mode and with avro/csv file format:

/Queries/QueryX-fileformat-local/clustered.sh

## Checking the results

Every query will produce the results either in hdfs then hbase.
You will find them in hdfs///queryResultsTmp/queryX

and in hbase by searching the queryX table (scan 'query1' for scanning the entire query 1 result)


## Exposed ports
you can explore all components by accessing the exposed ports on localhost:


```
http://localhost:8088/cluster - hadoop world
```
```
http://localhost:5555/nifi - nifi
```
```
http://localhost:50070 - hdfs
```
```
http://localhost:16010 - hbase
```



## Built With

* [Apache Hadoop HDFS](http://hadoop.apache.org/) - Distribuited file system
* [Apache Hadoop YARN](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html) - Resources manager
* [Apache SPARK](https://spark.apache.org/) - Unified Analytics Engine for Big Data
* [Apache NIFI](https://nifi.apache.org/) - Data Injection Framework
* [Apache HBASE](https://hbase.apache.org/) - Column-Family database
