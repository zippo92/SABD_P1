import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Query2 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(file_path);

        JavaPairRDD<Tuple2<Long, Integer>, Tuple2<Double, Double>> prova = rawCsv.map(line -> SmartPlugParser.parseCsv(line))
                .filter(plug -> plug.getProperty() == 0)
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>
                        (plug.getHouse_id(), SmartPlug.timeStampToInteger(plug.getTimestamp())),
                        new Tuple2<>(new Tuple2<>(plug.getValue(),plug.getValue()*plug.getValue()), 1)))
                .reduceByKey((tuple1,tuple2) -> new Tuple2<>(new Tuple2<>(tuple1._1._1 + tuple2._1._1,
                                tuple1._1._2 + tuple2._1._2), tuple1._2 + tuple2._2))
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1, plug._1._2),
                        new Tuple2<>(plug._2._1._1 / plug._2._2, plug._2._1._2 / plug._2._2)))
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1, plug._1._2),
                        new Tuple2<>(plug._2._1,
                                Math.sqrt(plug._2._2 - (plug._2._1 * plug._2._1)))));

//       JavaPairRDD<Integer, SmartPlug> hourlySmartPlug = rawCsv.map(line -> SmartPlugParser.parseCsv(line)).

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query2");
    }
}
