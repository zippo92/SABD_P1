import Utils.HDFSUtils;
import Utils.SmartPlug;
import Utils.SmartPlugParser;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


public class AvroRead {

    private static final String file_path = "hdfs://master:54310/FilesFromNifi/d14_filtered.csv.avro";

    public static void main(String[] args) {


        JavaRDD<SmartPlug> query = HDFSUtils.startSessionFromAvro()
                .filter(plug -> plug.getProperty()==0 && plug.getId()==3740246309L);


        for(SmartPlug smartPlug : query.take(1))
            System.out.println(smartPlug.toString());


    }

}
