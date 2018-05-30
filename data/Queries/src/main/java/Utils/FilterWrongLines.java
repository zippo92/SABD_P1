package Utils;

import Utils.HDFSUtils;
import Utils.SmartPlug;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Double;
import java.lang.Long;
import java.util.ArrayList;
import java.util.Iterator;

public class FilterWrongLines {

    public static void main(String[] args) throws IOException {



        JavaRDD<SmartPlug> smartPlugJavaRDD = HDFSUtils.startSession(args[0],"local");

        JavaPairRDD<Tuple5<Long,Long, Long, Integer, Integer>, Iterable<Tuple3<Double, Long, Long>>> allLines =
            smartPlugJavaRDD
            .filter(plug -> plug.getProperty() == 0)
            .mapToPair(plug -> new Tuple2<>(SmartPlug.getTimeZoneAndDay(plug.getHouse_id(),plug.getHousehold_id(), plug.getPlug_id(), plug.getTimestamp()), new Tuple3<>(plug.getValue(), plug.getTimestamp(), plug.getId())))
             .mapToPair(plug -> new Tuple2<>(plug._2._2(), new Tuple3<>(plug._1, plug._2._1(), plug._2._3())))
            .sortByKey(true)
            .mapToPair(plug -> new Tuple2<>(plug._2._1(), new Tuple3<>(plug._2._2(), plug._1, plug._2._3())))
            .groupByKey();


        ArrayList<Tuple5<Long,Long,Long,Integer,Integer>> wrongDays = new ArrayList<>();

        for (Tuple2<Tuple5<Long,Long, Long, Integer, Integer>, Iterable<Tuple3<Double, Long, Long>>> tupla : allLines.collect()) {
            Iterator iterator = tupla._2.iterator();
            Double precval = 0.0;

            while (iterator.hasNext()) {
                Tuple3<Double, Long, Long> next = (Tuple3<Double, Long, Long>) iterator.next();

                Double postval = next._1();

                if (postval - precval < -0.01) {

                    wrongDays.add(tupla._1);
                }
                precval = postval;
            }

        }

        if(wrongDays.size()!=0) {
            String days = "";

            for (Tuple5<Long,Long,Long, Integer, Integer> day : wrongDays)
            {
               days = days + day.toString().substring(1,day.toString().length()-1) + "\n";

            }
            HDFSUtils.writeOnHdfs(days,"hdfs://master:54310/FilesFromNifi/wrongRows.csv");

        }

    }

}
