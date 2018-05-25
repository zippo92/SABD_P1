package Utils;

import java.io.Serializable;

public class Query1Wrapper implements Serializable {
    private long row_id;
    private long house_id;

    public long getRow_id() {
        return row_id;
    }

    public void setRow_id(long row_id) {
        this.row_id = row_id;
    }

    public long getHouse_id() {
        return house_id;
    }

    public void setHouse_id(long house_id) {
        this.house_id = house_id;
    }
}
