import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Int;
import scala.Tuple2;
import scala.Tuple3;

public class Query2 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> rawCsv = sc.textFile(file_path);


        JavaPairRDD<Tuple2<Long,Integer>,Tuple2<Double,Double>>prova =
                /* Parse csv lines */
            rawCsv.map(line -> SmartPlugParser.parseCsv(line))
            /* Filter only energetic consume */
            .filter(plug -> plug.getProperty() == 0)
            /*Tuple : [(House_id,Plug_id,DD,TZ)(Val,Val)]*/
            .mapToPair(plug -> new Tuple2<>(SmartPlug.getTimeZoneAndDay(plug.getHouse_id(),plug.getPlug_id(),plug.getTimestamp()),new Tuple2<>(plug.getValue(),plug.getValue())))
            /*Calculate max and min of each (House_id,Plug_id,DD,TZ) -> (House_id,Plug_id,DD,TZ)(Max,Min) */
            .reduceByKey((tuple1,tuple2) -> new Tuple2<>(Math.max(tuple1._1,tuple2._1),Math.min(tuple1._1,tuple2._1)))
            /*Tuple: (house_id,DD,TZ)(Max-Min )*/
            .mapToPair(plug -> new Tuple2<>(new Tuple3<>(plug._1._1(),plug._1._3(),plug._1._4()), plug._2._1-plug._2._2))
            /*Sum of the mean by day -> (house_id,DD,TZ)(delta)*/
            .reduceByKey((x,y) -> x+y)
            /*Tuple: (house_id,TZ)(delta,delta,1)*/
            .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1(),plug._1._3()), new Tuple3<>(plug._2,plug._2,1)))
            /*Sum of the mean by day -> house_id,TZ)(delta,delta^2,counter)*/
            .reduceByKey((tuple1,tuple2) -> new Tuple3<>(tuple1._1() + tuple2._1(), tuple1._1()*tuple1._1() + tuple2._2()*tuple2._2(),tuple1._3()+tuple2._3()))
            /* Tuple: ((house_id, timezone), (mean, square mean)) */
            .mapToPair(plug -> new Tuple2<>(plug._1,new Tuple2<>(plug._2._1()/plug._2._3(),plug._2._2()/plug._2._3())))
            /* Tuple: ((house_id, timezone), (mean, standard deviation)) */
            .mapToPair(plug -> new Tuple2<>(plug._1, new Tuple2<>(plug._2._1, Math.sqrt(plug._2._2 - (plug._2._1)*(plug._2._1)))));

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query2");
    }
}
