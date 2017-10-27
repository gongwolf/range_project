package range_partition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCompare {
    public static void main(String args[]) throws ParseException {
        TimeCompare c = new TimeCompare();
        System.out.println(c.getPartitionType("23:40:28", "06:05:00", "18:20:00"));

    }


    public int getPartitionType(String cur_date, String Sunrise, String Sunset) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        Date Sunrise_D = null;
        Date Sunset_D = null;
        try {
            Sunrise_D = parser.parse(Sunrise);
            Sunset_D = parser.parse(Sunset);

            Date other = parser.parse(cur_date);

            if (other.before(Sunrise_D)) {
                return 0;
            } else if (other.after(Sunset_D)) {
                return 2;
            } else {
                return 1;
            }
        } catch (ParseException e) {
            System.out.println("There is something wrong with your time formation, please check it");
        }

        return -1;
    }
}
