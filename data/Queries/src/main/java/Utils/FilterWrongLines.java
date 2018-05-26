package Utils;

import Utils.HDFSUtils;
import Utils.SmartPlug;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Array;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class FilterWrongLines {

    public static void main(String[] args) throws FileNotFoundException {



        JavaRDD<SmartPlug> smartPlugJavaRDD = HDFSUtils.startSession(args[0],"local");

        JavaPairRDD<Tuple4<Long, Long, Integer, Integer>, Iterable<Tuple3<Double, Long, Long>>> allLines =
            smartPlugJavaRDD
            .filter(plug -> plug.getProperty() == 0)
            .mapToPair(plug -> new Tuple2<>(SmartPlug.getTimeZoneAndDay(plug.getHouse_id(), plug.getPlug_id(), plug.getTimestamp()), new Tuple3<>(plug.getValue(), plug.getTimestamp(), plug.getId())))
            .mapToPair(plug -> new Tuple2<>(plug._2._2(), new Tuple3<>(plug._1, plug._2._1(), plug._2._3())))
            .sortByKey(true)
            .mapToPair(plug -> new Tuple2<>(plug._2._1(), new Tuple3<>(plug._2._2(), plug._1, plug._2._3())))
            .groupByKey();


        ArrayList<Long> wrongLinesId = new ArrayList<>();

        for (Tuple2<Tuple4<Long, Long, Integer, Integer>, Iterable<Tuple3<Double, Long, Long>>> tupla : allLines.collect()) {
            Iterator iterator = tupla._2.iterator();
            Double precval = 0.0;

            while (iterator.hasNext()) {
                Tuple3<Double, Long, Long> next = (Tuple3<Double, Long, Long>) iterator.next();

                Double postval = next._1();
                Long postId = next._3();

                if (postval < precval) {
                    wrongLinesId.add(postId);
                }
                precval = postval;
            }

        }


        if (wrongLinesId.size() != 0) {
            JavaRDD<SmartPlug> filteredSmartPlug = smartPlugJavaRDD.filter(plug -> !wrongLinesId.contains(plug.getId()));
            filteredSmartPlug.saveAsTextFile("hdfs://master:54310/FilesFromNifi/d14_filtered.csv");
        }

    }

}
