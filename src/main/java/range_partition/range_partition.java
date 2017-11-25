package range_partition;

import javafx.util.Pair;
import range_pixel.PData;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class range_partition {
//    String basePath = "data/partition/";
    String basePath = "";
    String TimeData = basePath + "time.csv";
    String GPSData = basePath + "data.csv";
    HashMap<String, Pair<String, String>> timeObj = new HashMap<>();

    String prePath = basePath + "pre.csv";
    String dayPath = basePath + "day.csv";
    String postPath = basePath + "post.csv";

    File preF, dayF, postF;
    long pre_count, day_count, post_count;

    public static void main(String args[]) {
        range_partition rp = new range_partition();
        rp.cleanFiles();
        rp.readFilenName();
        rp.readTimeFile();
        rp.readGPSData();
    }

    private void cleanFiles() {
        preF = new File(this.prePath);
        dayF = new File(this.dayPath);
        postF = new File(this.postPath);

        if (preF.exists()) {
            preF.delete();
        }
        if (dayF.exists()) {
            dayF.delete();
        }
        if (postF.exists()) {
            postF.delete();
        }


        //write the title
        try (FileWriter fw = new FileWriter(preF, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("CowID,Date,Time,northing,easting");

            //close the stream
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter fw = new FileWriter(dayF, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("CowID,Date,Time,northing,easting");

            //close the stream
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (FileWriter fw = new FileWriter(postF, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("CowID,Date,Time,northing,easting");

            //close the stream
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readGPSData() {
        TimeCompare t = new TimeCompare();
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
                String date = infos[1];
                String time = infos[2];
                String sunrise = this.timeObj.get(date).getKey();
                String sunset = this.timeObj.get(date).getValue();
                int type = t.getPartitionType(time, sunrise, sunset);
//                System.out.println(date + " " + time + " " + sunrise + " " + sunset + " _> " + type);

                writeToFile(type, line);

//                if (linenumber == 1000) {
//                    break;
//                }

            }
            br.close();
        } catch (Exception e) {
            System.err.println("Can not open the GPS data file, please check it. ");
        }
        System.out.println("Read the GPS data file done" + "   " + linenumber);

        System.out.println("--------------------------------------------------");

        System.out.println("There are "+this.pre_count+" GPS records were wrote to pre.csv");
        System.out.println("There are "+this.day_count+" GPS records were wrote to day.csv");
        System.out.println("There are "+this.post_count+" GPS records were wrote to post.csv");
    }

    private void writeToFile(int type, String line) {
        String fileName = "";
        switch (type) {
            case 0:
                this.pre_count++;
                fileName = this.prePath;
                break;
            case 1:
                this.day_count++;
                fileName = this.dayPath;
                break;
            case 2:
                this.post_count++;
                fileName = this.postPath;
                break;
        }
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(line);

            //close the stream
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        System.out.println("--------------------------------------------------");
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
        System.out.println("Read the time file done" + "   " + linenumber);
        System.out.println("--------------------------------------------------");

    }
}
