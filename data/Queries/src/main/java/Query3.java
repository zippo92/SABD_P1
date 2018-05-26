import Utils.*;
import com.google.gson.Gson;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    public static void main(String[] args) throws IOException {
        JavaPairRDD<String,Double> query3 =
            /* Parse csv line */
            HDFSUtils.startSession(args[0],args[1])
            /* Filter only energetic consume */
            .filter(plug -> plug.getProperty()==0)
            /* map to tuple: ((concatenate_id,DD,TZ), (value, value)) */
            .mapToPair(tuple -> new Tuple2<>(SmartPlug.getTimeSlotAndDay(tuple.getHouse_id(),tuple.getHousehold_id(),tuple.getPlug_id(),tuple.getTimestamp()),
                new Tuple2<>(tuple.getValue(),tuple.getValue())))
            /* Calculate min and max of that slot: ((concatenate_id,DD,TZ), (max, min))*/
            .reduceByKey((tuple1,tuple2) -> new Tuple2<>(Math.max(tuple1._1,tuple2._1),Math.min(tuple1._2,tuple2._2)))
            /* map to tuple: ((concatenate_id,TZ), (delta, counter)) */
            .mapToPair(tuple -> new Tuple2<>(new Tuple2<>(tuple._1._1(),tuple._1._3()),new Tuple2<>(tuple._2._1-tuple._2._2,1)))
            /* Sum the values and the counter */
            .reduceByKey((tuple1, tuple2) -> new Tuple2<>(tuple1._1 + tuple2._1, tuple1._2 + tuple2._2))
            /* map to tuple: (concatenate_id, +- average (+ if slot 0, - else) */
            .mapToPair(tuple -> new Tuple2<>(tuple._1._1, Query3.convertValue(tuple._1._2, tuple._2._1, tuple._2._2)))
            /* Compute the sum (or difference) between timezone */
            .reduceByKey((x, y) -> x+ y)
            /* Swap trick */
            .mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1))
            /* Sort by value and re-swap */
            .sortByKey(false).mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1));

        query3.saveAsTextFile("hdfs://master:54310/queryResultsTmp/query3");

        List<Query3Wrapper> wrappers = new ArrayList<>();
        long id = 1;

        for(Tuple2<String, Double> tuple : query3.collect()){
            Query3Wrapper wrapper = new Query3Wrapper();
            wrapper.setRow_id(id);
            wrapper.setKey(tuple._1);
            wrapper.setValue(tuple._2);
            id++;
            wrappers.add(wrapper);
        }

        WriteJson writeJson = new WriteJson();
        Gson gson = new Gson();
        String gsonQuery3 = gson.toJson(wrappers);
        writeJson.write(gsonQuery3, "hdfs://master:54310/queryResults/query3/query3.json");

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
