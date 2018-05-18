import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class Query2 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(file_path);

        JavaPairRDD<Tuple2<Long, Integer>, Float> prova = rawCsv.map(line -> SmartPlugParser.parseCsv(line))
                .filter(plug -> plug.getProperty() == 0)
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>
                        (plug.getHouse_id(), SmartPlug.timeStampToInteger(plug.getTimestamp())), plug.getValue()))
                .reduceByKey((x, y) -> x + y);

//       JavaPairRDD<Integer, SmartPlug> hourlySmartPlug = rawCsv.map(line -> SmartPlugParser.parseCsv(line)).

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query2");

    }
}
