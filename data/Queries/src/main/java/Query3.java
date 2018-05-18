import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class Query3 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(file_path);

        JavaPairRDD<String, Double> prova =
                /* Parse csv line */
                rawCsv.map(line -> SmartPlugParser.parseCsv(line))
                /* Filter only energetic consume */
                .filter(plug -> plug.getProperty() == 0)
                /* Tuple: ((concatenate_id, timezone), (value, count)) */
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug.getHouse_id() + "_" +
                        plug.getHousehold_id() + "_" + plug.getPlug_id(),
                        SmartPlug.timeStampToFascia(plug.getTimestamp())),
                        new Tuple2<>(plug.getValue(), 1)))
                /* Sum the values and the counter */
                .reduceByKey((tuple1, tuple2) -> new Tuple2<>(tuple1._1 + tuple2._1, tuple1._2 + tuple2._2))
                /* Tuple: (concatenate_id, average) */
                .mapToPair(plug -> new Tuple2<>(plug._1._1, Query3.convertValue(plug._1._2, plug._2._1, plug._2._2)))
                /* Compute the different between timezone */
                .reduceByKey((x, y) -> x+ y)
                /* Swap trick */
                .mapToPair(plug -> new Tuple2<>(plug._2, plug._1))
                /* Sort by value and re-swap */
                .sortByKey(false).mapToPair(plug -> new Tuple2<>(plug._2, plug._1));

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query3");
    }

   public static Double convertValue(Integer hour, Double value, Integer count){

        Double avg = value/count;

        if(hour == 1){
            return -(avg);
        }
        else{
            return avg;
        }
   }
}
