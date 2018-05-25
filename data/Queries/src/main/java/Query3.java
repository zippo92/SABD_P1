import Utils.HDFSUtils;
import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple3;

/**
 * Si considerino le seguenti fasce di consumo dell’energia elettrica, differenziate in base all’ora e al
 giorno della settimana, ed a cui corrispondono differenti tariffe: fascia di punta, che si applica negli
 orari diurni dal luned`ı al venerd`ı (dalle 06:00 alle 17:59) ed a cui corrisponde una tariffa piu alta, e `
 fascia fuori punta, che si applica negli orari notturni dal luned`ı al venerd`ı (dalle 18:00 alle 05:59), nel
 fine settimana (sabato e domenica) e nei giorni festivi. Calcolare la classifica delle prese intelligenti in
 base alla differenza dei consumi energetici medi mensili tra la fascia di punta e la fascia fuori punta.
 Nella classifica le prese sono ordinate in modo decrescente, riportando, come primi elementi, le prese
 che non sfruttano la fascia fuori punta.
 *
 */
public class Query3 {


    public static void main(String[] args) {
        JavaPairRDD<String,Double> prova =
            /* Parse csv line */
        HDFSUtils.startSession(args[0])
                /* Filter only energetic consume */
        .filter(plug -> plug.getProperty()==0)
        /* map to tuple: ((concatenate_id,DD,TZ), (value, value)) */
        .mapToPair(plug -> new Tuple2<>(SmartPlug.getTimeSlotAndDay(plug.getHouse_id(),plug.getHousehold_id(),plug.getPlug_id(),plug.getTimestamp()),
            new Tuple2<>(plug.getValue(),plug.getValue())))
        /* Calculate min and max of that slot: ((concatenate_id,DD,TZ), (max, min))*/
        .reduceByKey((tuple1,tuple2) -> new Tuple2<>(Math.max(tuple1._1,tuple2._1),Math.min(tuple1._2,tuple2._2)))
        /* map to tuple: ((concatenate_id,TZ), (delta, counter)) */
        .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug._1._1(),plug._1._3()),new Tuple2<>(plug._2._1-plug._2._2,1)))
        //            /* Sum the values and the counter */
        .reduceByKey((tuple1, tuple2) -> new Tuple2<>(tuple1._1 + tuple2._1, tuple1._2 + tuple2._2))
        //            /* map to tuple: (concatenate_id, +- average (+ if slot 0, - else) */
        .mapToPair(plug -> new Tuple2<>(plug._1._1, Query3.convertValue(plug._1._2, plug._2._1, plug._2._2)))
        //            /* Compute the different between timezone */
        .reduceByKey((x, y) -> x+ y)
        //            /* Swap trick */
        .mapToPair(plug -> new Tuple2<>(plug._2, plug._1))
        //            /* Sort by value and re-swap */
        .sortByKey(false).mapToPair(plug -> new Tuple2<>(plug._2, plug._1));

        prova.saveAsTextFile("hdfs://master:54310/queryResults/query3");
    }

   public static Double convertValue(Integer hour, Double value, Integer count){

        Double avg = value/count;

        if(hour == 0){
            return avg;
        }
        else{
            return -(avg);
        }
   }
}
