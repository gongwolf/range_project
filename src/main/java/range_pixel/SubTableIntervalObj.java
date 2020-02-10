package range_pixel;

import java.util.ArrayList;
import java.util.Date;

public class SubTableIntervalObj {
    public Date start_date;
    public Date end_date;
    int id;
    ArrayList<DaysIntervalObj> daysintervalobjList = new ArrayList<>();

    @Override
    public String toString() {
        return "SubTableIntervalObj{" +
                "start_date=" + start_date +
                ", end_date=" + end_date +
                ", id=" + id +
                ", daysintervalobjList=" + daysintervalobjList +
                '}';
    }
}
