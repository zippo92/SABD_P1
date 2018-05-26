import Utils.*;
import com.google.gson.Gson;
import com.sun.rowset.internal.Row;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import scala.Int;
import scala.Tuple2;
import scala.Tuple3;

import scala.Tuple4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Per ogni casa, calcolare il consumo energetico medio e la sua deviazione standard nelle quattro fasce
 orarie: notte, dalle ore 00:00 alle ore 05:59; mattino, dalle 06:00 alle 11:59; pomeriggio, dalle 12:00
 alle 17:59; e sera, dalle 18:00 alle 23:59. Il consumo energetico di una casa e dato dalla somma dei `
 consumi energetici di ogni presa intelligente collocata nella casa
 */
public class Query2 {


    public static void main(String[] args) throws IOException {

        JavaPairRDD<Tuple2<Long, Integer>, Tuple2<Double, Double>> query2 =
            /* Parse csv or avro lines */
            HDFSUtils.startSession(args[0],args[1])
            /* Filter by 0 property */
            .filter(tuple -> tuple.getProperty() == 0)
            /*Map to tuple : [(House_id,Plug_id,DD,TZ)(Val,Val)]*/
            .mapToPair(tuple -> new Tuple2<>(SmartPlug.getTimeZoneAndDay(tuple.getHouse_id(),tuple.getPlug_id(),tuple.getTimestamp()),new Tuple2<>(tuple.getValue(),tuple.getValue())))
            /*Calculate max and min of each tuple4 -> (House_id,Plug_id,DD,TZ)(Max,Min) */
            .reduceByKey((tuple1,tuple2) -> new Tuple2<>(Math.max(tuple1._1,tuple2._1),Math.min(tuple1._2,tuple2._2)))
            /*Map to Tuple: (house_id,Plug_id,TZ)(delta,delta^2,counter )*/
            .mapToPair(tuple -> new Tuple2<>(new Tuple3<>(tuple._1._1(),tuple._1._2(),tuple._1._4()), new Tuple3<>(tuple._2._1-tuple._2._2, (tuple._2._1-tuple._2._2) * (tuple._2._1-tuple._2._2),1)))
            /*Sum of the mean of each day -> (house_id,Plug_id,TZ)(delta;delta^2,counter)*/
            .reduceByKey((tuple1,tuple2) -> new Tuple3<>(tuple1._1() + tuple2._1(), tuple1._2() + tuple2._2(),tuple1._3()+tuple2._3()))
            /*Calculate mean and std of each plug (house_id,TZ)(mean,std)*/
            .mapToPair(tuple ->
                {
                    Double mean = tuple._2._1()/tuple._2._3();
                    Double sq_mean = tuple._2._2()/tuple._2._3();
                    Double std = Math.sqrt(sq_mean - mean*mean);
                    return new Tuple2<>(new Tuple2<>(tuple._1._1(),tuple._1._3()),new Tuple2<>(mean,std));
                })
            /*sum mean and std of each house_id*/
            .reduceByKey((tuple1,tuple2) -> new Tuple2<>(tuple1._1+tuple2._1,tuple1._2 + tuple2._2));


        query2.saveAsTextFile("hdfs://master:54310/queryResultsTmp/query2");

        List<Query2Wrapper> wrappers = new ArrayList<>();
        long id = 1;

        for(Tuple2<Tuple2<Long, Integer>, Tuple2<Double, Double>> tupla : query2.collect()){
            Query2Wrapper wrapper = new Query2Wrapper();
            wrapper.setRow_id(id);
            wrapper.setKey(String.valueOf(tupla._1._1) + "_" + String.valueOf(tupla._1._2));
            wrapper.setMean(tupla._2._1);
            wrapper.setStd(tupla._2._2);
            id++;
            wrappers.add(wrapper);
        }

        WriteJson writeJson = new WriteJson();
        Gson gson = new Gson();
        String gsonQuery2 = gson.toJson(wrappers);
        writeJson.write(gsonQuery2, "hdfs://master:54310/queryResults/query2/query2.json");

    }
}
