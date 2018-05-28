package Utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkContextSingleton {

    private static SparkContextSingleton me;
    private JavaSparkContext sc;
    private SparkSession spark;

    public static SparkContextSingleton getMe(String local) {

        if (me == null) {

            me = new SparkContextSingleton(local);
            return me;

        }
        return me;
    }


    private SparkContextSingleton(String local)
    {

            SparkConf conf = new SparkConf().setAppName("Query");

            if (local.equals("local"))
                conf.setMaster("local");

            sc = new JavaSparkContext(conf);

    }

    public JavaSparkContext getContext() {
        return sc;
    }

}
