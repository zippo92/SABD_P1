import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class Query2 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(file_path);

//        JavaRDD<SmartPlug> smartPlugs =
//                rawCsv.map(line -> SmartPlugParser.parseCsv(line));

//        JavaPairRDD<Integer, SmartPlug> hourlySmartPlug = rawCsv.map(line -> SmartPlugParser.parseCsv(line)).




    }
}
