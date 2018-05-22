$NIFI_HOME/bin/nifi.sh start
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh
$SPARK_HOME/sbin/start-all.sh
$HBASE_HOME/bin/start-hbase.sh
