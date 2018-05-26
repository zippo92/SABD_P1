import Utils.HDFSUtils;
import Utils.Query1Wrapper;
import Utils.SmartPlugParser;
import Utils.WriteJson;
import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Individuare le case con consumo di potenza istantaneo maggiore o uguale a 350 Watt
 */
public class Query1 {

    public static void main(String[] args) throws IOException {



        JavaRDD<Long> houseId =
            /* Parse csv or avro lines and filter by 1 property */
            HDFSUtils.startSession(args[0],args[1])
             .filter(plug -> plug.getProperty()==1)
            /* Map to tuple: ((house_id, timestamp), power_value) */
            .mapToPair(tuple -> new Tuple2<>(new Tuple2<>(tuple.getHouse_id(),tuple.getTimestamp()),tuple.getValue()))
            /* Sum values */
            .reduceByKey((x, y) -> x + y)
            /* Filter values > 350 */
            .filter(plug -> plug._2>350)
            /* Group by house_id */
            .groupBy(plug -> plug._1._1)
            /* Take only house_id */
            .map(plug -> plug._1);

        /*Save the result on hdfs*/
        houseId.saveAsTextFile("hdfs://master:54310/queryResultsTmp/query1");

        /*JSONize the result and save on hdfs (because NIFI will take it, convert to avro and save to Hbase) */
        List<Query1Wrapper> wrappers = new ArrayList<>();
        long id = 1;

        for(Long house : houseId.collect()){
            Query1Wrapper wrapper = new Query1Wrapper();
            wrapper.setRow_id(id);
            wrapper.setHouse_id(house);
            id++;
            wrappers.add(wrapper);
        }

        WriteJson writeJson = new WriteJson();
        Gson gson = new Gson();
        String query1 = gson.toJson(wrappers);
        writeJson.write(query1, "hdfs://master:54310/queryResults/query1/query1.json");

    }
}
