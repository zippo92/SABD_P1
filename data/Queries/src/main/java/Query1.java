import Utils.HDFSUtils;
import Utils.SmartPlugParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;

/**
 *
 * Individuare le case con consumo di potenza istantaneo maggiore o uguale a 350 Watt
 */
public class Query1 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";

    public static void main(String[] args) throws IOException {



        JavaRDD<Long> houseId =
                /* Parse csv lines */
                HDFSUtils.startSessionFromCsv()
                /* Tuple: ((house_id, timestamp), power_value) */
                .mapToPair(plug -> new Tuple2<>(new Tuple2<>(plug.getHouse_id(),plug.getTimestamp()),plug.getValue()))
                /* Sum values */
                .reduceByKey((x, y) -> x + y)
                /* Filter values > 350 */
                .filter(plug -> plug._2>350)
                /* Group by house_id */
                .groupBy(plug -> plug._1._1)
                /* Take only house_id */
                .map(plug -> plug._1);

        houseId.saveAsTextFile("hdfs://master:54310/queryResults/query1");
    }
}
