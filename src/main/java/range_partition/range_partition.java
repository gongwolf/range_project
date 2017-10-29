package range_partition;

import javafx.util.Pair;
import range_pixel.PData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

public class range_partition {
    String TimeData = "data/partition/time.csv";
    String GPSData = "data/partition/data.csv";
    HashMap<String, Pair<String, String>> timeObj = new HashMap<>();

    public static void main(String args[]) {
        range_partition rp = new range_partition();
        rp.readFilenName();
        rp.readTimeFile();
        rp.readGPSData();
    }

    private void readGPSData() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        int linenumber = 0;
        try {
            br = new BufferedReader(new FileReader(this.GPSData));
            String line = null;
            while ((line = br.readLine()) != null) {
                linenumber++;
                //jump the header
                if (linenumber == 1) {
                    continue;
                }

                String infos[] = line.split(",");
                

            }
            br.close();
        } catch (Exception e) {
            System.err.println("Can not open the GPS data file, please check it. ");
        }
        System.out.println("read the GPS data file done" + "   " + linenumber);
    }

    private void readFilenName() {
        InputStreamReader inp = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(inp);
        String str = null;
        try {
            System.out.println("Enter cordination file name (Default: data.csv): ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.GPSData = str;
            }

            System.out.println("Enter time file name (Default: time.csv): ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.TimeData = str;
            }
        } catch (IOException e) {
            System.err.println("There is something wrong with your input of the file name, please check it.");
            System.exit(0);
        }
    }

    public void readTimeFile() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        int linenumber = 0;
        try {
            br = new BufferedReader(new FileReader(this.TimeData));
            String line = null;
            while ((line = br.readLine()) != null) {
                linenumber++;
                //jump the header
                if (linenumber == 1) {
                    continue;
                }

                String infos[] = line.split(",");
                String date = infos[0];
                String sunrise_time = infos[1];
                String sunset_time = infos[2];
                this.timeObj.put(date, new Pair<String, String>(sunrise_time, sunset_time));

            }
            br.close();
        } catch (Exception e) {
            System.err.println("Can not open the time file, please check it. ");
        }
        System.out.println("read the time file done" + "   " + linenumber);

    }
}
