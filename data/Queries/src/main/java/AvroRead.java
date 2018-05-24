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

        SparkSession spark = SparkSession.builder().master("local").getOrCreate();

// Creates a DataFrame from a specified file
        Dataset<Row> df = spark.read().format("com.databricks.spark.avro")
                .load(file_path);

        JavaRDD<Row> rawCsv = df.toJavaRDD();


        JavaRDD<SmartPlug> prova =
            /* Parse csv lines */
                rawCsv.map(line -> SmartPlugParser.parseCsv(line.toString()))
            /* Filter only energetic consume */
                        .filter(plug -> plug.getProperty() == 0);


        for(SmartPlug smartPlug : prova.take(10))
        {

            System.out.println(smartPlug.toString());
        }


    }

}
