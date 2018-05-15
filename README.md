# Project Title

Progetto 1 - Sistemi e Architetture per Big data

### Quickstart

Run for build the container:

```
sudo docker build -t mysparkimage .
```

Then

```
./create.sh
```
use this to create 4 images, of which 1 master and 3 slaves

```
./destroy.sh
```
and this to destroy them


once inside the master image

```
./data/script/startAll.sh
```
to initialize all the environment (hdfs - yarn - spark - nifi)

## Inject the dataset

Go to

```
localhost:5555/nifi
```

select all the processors (CTRL+A) and press the play button ▻


## Built With

* [Apache Hadoop HDFS](http://hadoop.apache.org/) - Distribuited file system
* [Apache Hadoop YARN](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html) - Resources manager
* [Apache SPARK](https://spark.apache.org/) - Unified Analytics Engine for Big Data
* [Apache NIFI](https://nifi.apache.org/) - Data Injection Framework
