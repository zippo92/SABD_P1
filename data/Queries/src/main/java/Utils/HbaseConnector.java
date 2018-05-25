package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HbaseConnector {
    private static HbaseConnector hbaseConnector = null;

    private Configuration config;
    private Connection connection;
    private Admin admin;

    private HbaseConnector() throws IOException {
        config = HBaseConfiguration.create();
        String path = this.getClass()
                .getClassLoader()
                .getResource("$HBASE_HOME/conf/hbase-site.xml")
                .getPath();
        config.addResource(new Path(path));
        connection = ConnectionFactory.createConnection(config);
        admin = connection.getAdmin();
    }

    public static HbaseConnector getInstance() throws IOException {
        if(hbaseConnector == null){
            hbaseConnector = new HbaseConnector();
        }
        return hbaseConnector;
    }

    public Admin getAdmin(){
        return admin;
    }

    public void closeConnection() throws IOException {
        connection.close();
    }

    public void closeAdmin() throws IOException {
        admin.close();
    }
}
