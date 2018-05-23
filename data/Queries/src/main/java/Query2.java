import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Int;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;


/**
 * Per ogni casa, calcolare il consumo energetico medio e la sua deviazione standard nelle quattro fasce
 orarie: notte, dalle ore 00:00 alle ore 05:59; mattino, dalle 06:00 alle 11:59; pomeriggio, dalle 12:00
 alle 17:59; e sera, dalle 18:00 alle 23:59. Il consumo energetico di una casa e dato dalla somma dei `
 consumi energetici di ogni presa intelligente collocata nella casa
 */
public class Query2 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query2");
        JavaSparkContext sc = new JavaSparkContext(conf);
//
        JavaRDD<String> rawCsv = sc.textFile(file_path);

        JavaPairRDD<Tuple2<Long,Integer>,Tuple2<Double,Double>> prova =
            /* Parse csv lines */
            rawCsv.map(line -> SmartPlugParser.parseCsv(line))
            /* Filter only energetic consume */
            .filter(plug -> plug.getProperty() == 0)
            /*Map to tuple : [(House_id,Plug_id,DD,TZ)(Val,Val)]*/
            .mapToPair(plug -> new Tuple2<>(SmartPlug.getTimeZoneAndDay(plug.getHouse_id(),plug.getPlug_id(),plug.getTimestamp()),new Tuple2<>(plug.getValue(),plug.getValue())))
            /*Calculate max and min of each (House_id,Plug_id,DD,TZ) -> (House_id,Plug_id,DD,TZ)(Max,Min) */
            .reduceByKey((tuple1,tuple2) -> new Tuple2<>(Math.max(tuple1._1,tuple2._1),Math.min(tuple1._2,tuple2._2)))
            /*Map to Tuple: (house_id,DD,TZ)(Max-Min )*/
            .mapToPair(plug -> new Tuple2<>(new Tuple3<>(plug._1._1(),plug._1._3(),plug._1._4()), plug._2._1-plug._2._2))
            /*Sum of the mean by day -> (house_id,DD,TZ)(delta)*/
            .reduceByKey((x,y) -> x+y)
////            /*Map to Tuple: (house_id,TZ)(delta,delta,1)*/
            .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1(),plug._1._3()), new Tuple3<>(plug._2, plug._2 * plug._2,1)))
//            /*Sum of the mean by day -> house_id,TZ)(delta,delta^2,counter)*/
            .reduceByKey((tuple1,tuple2) -> new Tuple3<>(tuple1._1() + tuple2._1(), tuple1._2() + tuple2._2(),tuple1._3()+tuple2._3()))
////            /* Map to tuple: ((house_id, timezone), (mean, square mean)) */
            .mapToPair(plug -> new Tuple2<>(plug._1,new Tuple2<>(plug._2._1()/plug._2._3(),plug._2._2()/plug._2._3())))
//////            /* Map to tuple: ((house_id, timezone), (mean, standard deviation)) */
            .mapToPair(plug -> new Tuple2<>(plug._1, new Tuple2<>(plug._2._1, Math.sqrt(plug._2._2 - (plug._2._1)*(plug._2._1)))));

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query2");
    }
}
