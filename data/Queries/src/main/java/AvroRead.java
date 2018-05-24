import Utils.HDFSUtils;
import Utils.SmartPlug;
import Utils.SmartPlugParser;
import jdk.incubator.http.internal.frame.DataFrame;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import org.apache.spark.sql.*;
import org.apache.spark.sql.functions;


public class AvroRead {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv.avro";

    public static void main(String[] args) {


        JavaRDD<SmartPlug> query = HDFSUtils.startSessionFromAvro()
                .filter(plug -> plug.getProperty()==0);


        for(SmartPlug smartPlug : query.take(10))
            System.out.println(smartPlug.toString());


    }

}
