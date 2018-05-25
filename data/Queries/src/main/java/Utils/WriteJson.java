package Utils;

import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class WriteJson {

    public void write(String json, String path) throws IOException {

        Configuration configuration = new Configuration();
        URI toUri = URI.create(path);
        FileSystem fs = FileSystem.get(toUri,configuration);

        FSDataOutputStream out = fs.create(new Path(toUri));

        out.writeBytes(json);
        out.close();
    }
}
