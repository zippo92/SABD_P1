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

public class SmartPlug {


    long id;
    long timestamp;
    float value;
    long property;
    long plug_id;
    long household_id;
    long house_id;

    public SmartPlug(long id, long timestamp, float value, long property, long plug_id, long household_id, long house_id) {
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
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
