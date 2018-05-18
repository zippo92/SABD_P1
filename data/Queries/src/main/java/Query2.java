import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class Query2 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(file_path);

        JavaPairRDD<Tuple2<Long, Integer>, Tuple2<Double, Double>> prova =
                /* Parse csv lines */
                rawCsv.map(line -> SmartPlugParser.parseCsv(line))
                /* Filter only energetic consume */
                .filter(plug -> plug.getProperty() == 0)
                /* Tuple: ((house_id, time_zone), ((value, square value), count)) */
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>
                        (plug.getHouse_id(), SmartPlug.timeStampToInteger(plug.getTimestamp())),
                        new Tuple2<>(new Tuple2<>(plug.getValue(),plug.getValue()*plug.getValue()), 1)))
                /* Sum the values (non squared and squared) and sum counter */
                .reduceByKey((tuple1,tuple2) -> new Tuple2<>(new Tuple2<>(tuple1._1._1 + tuple2._1._1,
                                tuple1._1._2 + tuple2._1._2), tuple1._2 + tuple2._2))
                /* Tuple: ((house_id, timezone), (mean, square mean)) */
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1, plug._1._2),
                        new Tuple2<>(plug._2._1._1 / plug._2._2, plug._2._1._2 / plug._2._2)))
                /* Tuple: ((house_id, timezone), (mean, standard deviation)) */
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1, plug._1._2),
                        new Tuple2<>(plug._2._1,
                                Math.sqrt(plug._2._2 - (plug._2._1 * plug._2._1)))));

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query2");
    }
}
