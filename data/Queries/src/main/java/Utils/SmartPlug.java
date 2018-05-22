package Utils;


//id,timestamp,value,property,plug id,household id,house id
//        dove:
//        • id e un identificatore univoco della misura (intero senza segno a 32 bit); `
//        • timestamp e il timestamp (secondi dal 1 gennaio 1970, 00:00:00 GMT; intero senza segno a 32 `
//        bit);
//        • value e la misura (numero in virgola mobile a 32 bit); `
//        • property identifica la tipologia di misura: 0 per l’energia totale consumata, 1 per la potenza
//        istantanea consumata (booleano);
//        1
//        • plug id e l’identificatore, univoco per famiglia, della presa intelligente (intero senza segno a 32 bit); `
//        • household id e l’identificatore, univoco per casa, della famiglia dove la presa intelligente ` e loca- `
//        lizzata (intero senza segno a 32 bit);
//        • house id e l’id

import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmartPlug {


    long id;
    long timestamp;
    double value;
    long property;
    long plug_id;
    long household_id;
    long house_id;

    public SmartPlug(long id, long timestamp, double value, long property, long plug_id, long household_id, long house_id) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
        this.plug_id = plug_id;
        this.household_id = household_id;
        this.house_id = house_id;
        this.property = property;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public long getPlug_id() {
        return plug_id;
    }

    public void setPlug_id(int plug_id) {
        this.plug_id = plug_id;
    }

    public long getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(int household_id) {
        this.household_id = household_id;
    }

    public long getHouse_id() {
        return house_id;
    }

    public void setHouse_id(int house_id) {
        this.house_id = house_id;
    }

    public static Tuple4<Long,Long,Integer,Integer> getTimeZoneAndDay(long house_id,long plug_id, long timestamp){
        Date date = new Date(timestamp*1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("HH");
        String java_date = jdf.format(date);
        int hours = Integer.valueOf(java_date);
        int timeZone = 0;

        if(hours >= 0 && hours <= 5){
            timeZone = 0;
        }
        else if(hours >= 6 && hours <= 11){
            timeZone = 1;
        }
        else if(hours >= 12 && hours <= 17){
            timeZone = 2;
        }
        else if(hours >= 18 && hours <= 23){
            timeZone = 3;
        }

        jdf = new SimpleDateFormat("dd");
        int day = Integer.valueOf(jdf.format(date));



        return new Tuple4<>(house_id,plug_id,day,timeZone);
    }

    public static Tuple3<String,Integer,Integer> getTimeSlotAndDay(long house_id,long household_id,long plug_id,long timestamp){
        Date date = new Date(timestamp*1000L);
        SimpleDateFormat jdf_hh = new SimpleDateFormat("HH");
        SimpleDateFormat jdf_eeee = new SimpleDateFormat("EEEE");
        SimpleDateFormat jdf_dd = new SimpleDateFormat("dd");

        int hours = Integer.valueOf(jdf_hh.format(date));
        String dayName = jdf_eeee.format(date);
        int dd = Integer.valueOf(jdf_dd.format(date));


        String id = house_id + "_" + household_id + "_" + plug_id;

        if(dayName.equals("Saturday") || dayName.equals("Sunday"))
            return new Tuple3<>(id,dd,3);
        else if(hours>=0 && hours<6)
            return new Tuple3<>(id,dd,1);
        else if(hours>=6 && hours<18)
            return new Tuple3<>(id, dd, 0);
        else if (hours>=18 && hours <= 23)
            return new Tuple3<>(id, dd, 2);
        else
            return null;
    }


    @Override
    public String toString() {



        return "SmartPlug{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", value=" + value +
                ", property=" + property +
                ", plug_id=" + plug_id +
                ", household_id=" + household_id +
                ", house_id=" + house_id +
                '}';
    }
}
