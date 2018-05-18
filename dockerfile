FROM matnar/hadoop

# # Scala
RUN wget https://downloads.lightbend.com/scala/2.12.6/scala-2.12.6.tgz ; tar -zxf scala-2.12.6.tgz -C /usr/local/ ; rm scala-2.12.6.tgz
RUN cd /usr/local && ln -s ./scala-2.12.6 scala

ENV SCALA_HOME /usr/local/scala
ENV PATH $PATH:$SCALA_HOME/bin

# # Spark
RUN wget http://it.apache.contactlab.it/spark/spark-2.3.0/spark-2.3.0-bin-hadoop2.7.tgz ; tar -zxf spark-2.3.0-bin-hadoop2.7.tgz -C /usr/local/ ; rm spark-2.3.0-bin-hadoop2.7.tgz
RUN cd /usr/local && ln -s ./spark-2.3.0-bin-hadoop2.7 spark
#RUN mkdir /usr/local/spark/sample-data
ENV SPARK_HOME /usr/local/spark
ENV PATH $PATH:$SPARK_HOME/bin
RUN mkdir $SPARK_HOME/yarn-remote-client

RUN apt-get install nano;

#YARN->SPARK CONFIG
#ADD sample-data /usr/local/spark/sample-data
ADD config/yarn-remote-client/core-site.xml $SPARK_HOME/external/spark-native-yarn/conf/
ADD config/yarn-remote-client/yarn-site.xml $SPARK_HOME/external/spark-native-yarn/conf/
ADD config/yarn-site.xml $HADOOP_PREFIX/etc/hadoop/yarn-site.xml
RUN cd $SPARK_HOME/conf; cp spark-env.sh.template spark-env.sh; echo "export SPARK_JAVA_OPTS=-Dspark.driver.port=53411\nexport HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop\nexport YARN_CONF_DIR=$HADOOP_YARN_HOME/etc/hadoop\nSPARK_MASTER_IP=master" >> spark-env.sh;
RUN cd $SPARK_HOME/conf; cp spark-defaults.conf.template spark-defaults.conf; echo "spark.master            yarn\nspark.serializer        org.apache.spark.serializer.KryoSerializer" >> spark-defaults.conf;
RUN cd $SPARK_HOME/conf; echo "slave1\nslave2\nslave3" > slaves;
ENV PATH $PATH:$SPARK_HOME/bin:$HADOOP_PREFIX/bin
#APACHE NIFI
RUN wget http://it.apache.contactlab.it/nifi/1.6.0/nifi-1.6.0-bin.tar.gz; tar -zxf nifi-1.6.0-bin.tar.gz -C /usr/local/ ; rm nifi-1.6.0-bin.tar.gz
RUN cd /usr/local && ln -s ./nifi-1.6.0 nifi
ENV NIFI_HOME /usr/local/nifi
RUN cd $NIFI_HOME/conf; mv nifi.properties nifi.properties-old;
ADD config/nifi.properties $NIFI_HOME/conf
ADD config/flow.xml.gz $NIFI_HOME/conf


#Yarn ports
EXPOSE 8030 8031 8032 8033 8040 8042 8088
# #Other ports
EXPOSE 49707 2122 53411 7077 56302 54310


#GUIDA INSTALLAZIONE HBASE https://medium.com/@yzhong.cs/hbase-installation-step-by-step-guide-cb73381a7a4c
