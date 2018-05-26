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



    public static JavaRDD<SmartPlug> startSession(String format, String local)
    {

        if(format.equals("csv"))
            return HDFSUtils.startSessionFromCsv(local);
        if(format.equals("avro"))
            return HDFSUtils.startSessionFromAvro(local);

        else
            return null;
    }


    public static JavaRDD<SmartPlug> startSessionFromAvro(String local)
    {

        SparkSession spark;

        if(local.equals("local"))
            spark = SparkSession.builder().master("local").getOrCreate();
        if(local.equals("yarn"))
            spark = SparkSession.builder().getOrCreate();
        else
            return null;

// Creates a DataFrame from a specified file
        Dataset<Row> df = spark.read().format("com.databricks.spark.avro")
                .load(avro_path);

        JavaRDD<Row> rawCsv = df.toJavaRDD();

        JavaRDD<SmartPlug> dataset = rawCsv.map(line -> SmartPlugParser.parseCsv(line.toString().substring(1,line.toString().length()-1)));


        return dataset;


    }


    public static JavaRDD<SmartPlug> startSessionFromCsv(String local)
    {


        SparkConf conf = new SparkConf().setAppName("Query");

        if(local.equals("local"))
            conf.setMaster("local");


        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(csv_path);

        JavaRDD<SmartPlug> dataset =
                /* Parse csv lines */
                rawCsv.map(line -> SmartPlugParser.parseCsv(line));

        return dataset;
    }

}
