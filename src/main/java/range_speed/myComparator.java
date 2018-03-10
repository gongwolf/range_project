package range_speed;

import javafx.util.Pair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class myComparator {
}

class SortByTime implements Comparator<Pair<String, double[]>> {
    DateFormat TimeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    public int compare(Pair<String, double[]> n1, Pair<String, double[]> n2) {
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = this.TimeFormatter.parse(n1.getKey());
            d2 = this.TimeFormatter.parse(n2.getKey());


            if (d1.before(d2)) {
                return -1;
            } else if (d1.after(d2)) {
                return 1;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            System.out.println("There is something wrong with your time formation, please check it");
            System.exit(0);
        }
        return 0;

    }
}
