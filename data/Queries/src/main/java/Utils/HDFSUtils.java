package Utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class HDFSUtils {
    private static final String csv_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";
    private static final String avro_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv.avro";



    public static JavaRDD<SmartPlug> startSession(String conf)
    {

        if(conf.equals("csv"))
            return HDFSUtils.startSessionFromCsv();
        if(conf.equals("avro"))
            return HDFSUtils.startSessionFromAvro();

        else
            return null;
    }


    public static JavaRDD<SmartPlug> startSessionFromAvro()
    {

        SparkSession spark = SparkSession.builder().master("local").getOrCreate();

// Creates a DataFrame from a specified file
        Dataset<Row> df = spark.read().format("com.databricks.spark.avro")
                .load(avro_path);

        JavaRDD<Row> rawCsv = df.toJavaRDD();

        JavaRDD<SmartPlug> dataset = rawCsv.map(line -> SmartPlugParser.parseCsv(line.toString().substring(1,line.toString().length()-1)));


        return dataset;


    }


    public static JavaRDD<SmartPlug> startSessionFromCsv()
    {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(csv_path);

        JavaRDD<SmartPlug> dataset =
                /* Parse csv lines */
                rawCsv.map(line -> SmartPlugParser.parseCsv(line));

        return dataset;


    }

}
