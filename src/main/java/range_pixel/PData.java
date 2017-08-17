package range_pixel;

public class PData {

    public String gpsId, cowId, date;
    public double northing, easting;

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    private boolean isNull;

    public PData() {
        this.isNull = true;
    }

    public void initlizePData() {
        this.gpsId = this.cowId = this.date = "";
        this.northing = this.easting = 0.0;
    }

    public boolean isnull() {
        return this.isNull;
    }

    public void setAttrs(String[] infos) {
        gpsId = infos[0];
        cowId = infos[1];
        date = infos[2];
        northing = Double.parseDouble(infos[3]);
        easting = Double.parseDouble(infos[4]);
    }
}
