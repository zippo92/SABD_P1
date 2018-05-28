package Utils;

import org.codehaus.jackson.map.ObjectMapper;
import scala.Tuple4;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SmartPlugParser {

    public static SmartPlug parseCsv(String line) {

        SmartPlug smartPlug = null;

        String[] split = line.split(",");

//        smartPlug = new SmartPlug(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Float.valueOf(split[2]),Integer.parseInt(split[3]),Integer.parseInt(split[4]),Integer.parseInt(split[5]),Integer.parseInt(split[6]));
        smartPlug = new SmartPlug(Long.parseLong(split[0]),Long.parseLong(split[1]),Double.valueOf(split[2]),Long.parseLong(split[3]),Long.parseLong(split[4]),Long.parseLong(split[5]),Long.parseLong(split[6]));


        return smartPlug;
    }

   public static Tuple4<Long, Long, Integer, Integer> parseWrongLines(String line)
   {
       String[] split = line.split(",");

       return new Tuple4<>(Long.parseLong(split[0]),Long.parseLong(split[1]),Integer.parseInt(split[2]),Integer.parseInt(split[3]));

   }


}
