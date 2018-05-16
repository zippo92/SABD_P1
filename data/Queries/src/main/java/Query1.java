import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * Individuare le case con consumo di potenza istantaneo maggiore o uguale a 350 Watt
 */
public class Query1 {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv";

    public static void main(String[] args) throws IOException {


        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rawCsv = sc.textFile(file_path);

        JavaRDD<SmartPlug> smartPlugs =
                rawCsv.map(line -> SmartPlugParser.parseCsv(line));

//
//        JavaRDD<SmartPlug> houseId = smartPlugs.filter(plug -> plug.getProperty()==1 && plug.getValue()>350);
//
//        JavaPairRDD<Long, Iterable<SmartPlug>> boh = houseId.groupBy(plug -> plug.getHouse_id());
//
//
//        JavaRDD<Long> casette = boh.map(plug -> plug._1);

        JavaRDD<Long> houseId = smartPlugs
            .filter(plug -> plug.getProperty()==1 && plug.getValue()>350)
            .groupBy(plug -> plug.getHouse_id())
            .map(plug -> plug._1);


        houseId.saveAsTextFile("hdfs://master:54310/queryResults/query1");


    }






}
