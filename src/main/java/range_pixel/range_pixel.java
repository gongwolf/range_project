package range_pixel;

import javafx.util.Pair;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class range_pixel {
    String pixelPath = "pixel.csv";
    //    String GPSData = "data/pixel/sample_gps.csv";
    String GPSData = "coordinate_points.csv";


    HashMap<Long, Pair<Double, Double>> pixelList = new HashMap<Long, Pair<Double, Double>>(); //pixel_id -> <northing,easting>
    HashMap<String, HashMap<Long, HashSet<String>>> result = new HashMap<>(); //cowID—> Hashmap<pixel_id,data in the pixel of the cow>
    HashMap<String, HashMap<Long, Integer>> visited_result = new HashMap<>(); //cowID—> Hashmap<pixel_id,data in the pixel of the cow>
    HashMap<String, HashMap<Long, HashMap<Long, HashSet<String>>>> yearInfos = new HashMap<>();
    private int range_size = 30;
    private int min_speed = 5;
    private int max_speed = 100;
    private HashMap<Long, String> pixel_extra_info = new HashMap<>();//pix
    private String extra_title="";

    public static void main(String args[]) {
        range_pixel rp = new range_pixel();
        rp.readTheFileName();
        rp.loadPixelData();
//        System.out.println(rp.pixelList.size());
        rp.readGPSData();
//        System.out.println(rp.result.size());
//        rp.printVisitDateWithCowIDandPid("953","113");

        rp.printResult1();
        rp.setYearinfos();
        rp.printResult2();
        rp.printResult3();
        rp.printResult4();
    }

    private void readTheFileName() {
        InputStreamReader inp = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(inp);
        String str = null;
        try {
            System.out.println("Enter cordination file name (Default: coordinate_points.csv): ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.pixelPath = str;
            }
            //process weather file names
            System.out.println("Enter Pixel file name (Default: pixel.csv) : ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.GPSData = str;
            }

            System.out.println("Enter meters of the range (Default: 30) : ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.range_size = Integer.parseInt(str);
            }

            System.out.println("Enter min speed (Default: 5) : ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.min_speed = Integer.parseInt(str);
            }

            System.out.println("Enter max speed (Default: 100) : ");
            str = in.readLine();
            if (str.trim().length() > 0) {
                this.max_speed = Integer.parseInt(str);
            }


        } catch (IOException e) {
            System.err.println("There is something wrong with your input of the file name, please check it.");
        }

    }

    private void setYearinfos() {
        for (Map.Entry<String, HashMap<Long, HashSet<String>>> cow_infos : this.result.entrySet()) {
            String cowid = cow_infos.getKey();
            for (Map.Entry<Long, HashSet<String>> pixel_infos : cow_infos.getValue().entrySet()) {
                long pixel_id = pixel_infos.getKey();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                for (String dateStr : pixel_infos.getValue()) {
                    try {
                        c.setTime(ft.parse(dateStr));
                        long year = c.get(Calendar.YEAR);


                        HashMap<Long, HashMap<Long, HashSet<String>>> years = yearInfos.get(cowid);
                        if (years == null) {
                            years = new HashMap<>();
                        }


                        HashMap<Long, HashSet<String>> pixels = years.get(year);
                        if (pixels == null) {
                            pixels = new HashMap<>();
                        }

                        HashSet<String> dateList = pixels.get(pixel_id);
                        if (dateList == null) {
                            dateList = new HashSet<>();
                        }

                        dateList.add(dateStr);
                        pixels.put(pixel_id, dateList);
                        years.put(year, pixels);
                        yearInfos.put(cowid, years);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        System.out.println(yearInfos.size());
    }


    private void printResult4() {
        File file = new File("result4.csv");

        if (file.exists()) {
            file.delete();
        }

        try (FileWriter fw = new FileWriter("result4.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("Cow_id,Pixel ID,Number of visited times,time spent,"+this.extra_title+"Northing,Easting");
            for (Map.Entry<String, HashMap<Long, Integer>> cow_infos : this.visited_result.entrySet()) {
                String cowid = cow_infos.getKey();
                for (Map.Entry<Long, Integer> pixel_infos : cow_infos.getValue().entrySet()) {
                    long pixel_id = pixel_infos.getKey();
                    int times = pixel_infos.getValue();
                    long spent = times * 5;
                    String pixelVegClass = this.pixel_extra_info.get(pixel_id);
                    out.println(cowid + "," + pixel_id + "," + times + "," + spent + "," + pixelVegClass
                             + this.pixelList.get(pixel_id).getKey() + "," + this.pixelList.get(pixel_id).getValue());
                }
            }


            out.close();

            System.out.println("Done!! See result4.csv,  how much time each animal spends in each cell (in minutes).");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void printResult3() {
        File file = new File("result3.csv");

        if (file.exists()) {
            file.delete();
        }

        try (FileWriter fw = new FileWriter("result3.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {


            out.println("Cow_id, Years, Number_of_Pixels, average days the cow visited in each pixel in this year, average interval days the cow visited back in each pixel");
            for (Map.Entry<String, HashMap<Long, HashMap<Long, HashSet<String>>>> cow_infos : yearInfos.entrySet()) {
                String cowid = cow_infos.getKey();
                HashMap<Long, ArrayList<Double>> yearsCounts = new HashMap<>();
                for (Map.Entry<Long, HashMap<Long, HashSet<String>>> years_obj : cow_infos.getValue().entrySet()) {
                    long year = years_obj.getKey();
                    for (Map.Entry<Long, HashSet<String>> pixel_infos : years_obj.getValue().entrySet()) {
                        long pixel_id = pixel_infos.getKey();
                        int size = pixel_infos.getValue().size();
                        long diff = getDifferDate(pixel_infos.getValue());
                        double period = size - 1 != 0 ? (double) diff / (size - 1) : 0;

                        if (period != 0) {
                            ArrayList<Double> year_obj = yearsCounts.get(year);
                            if (year_obj == null) {
                                year_obj = new ArrayList<>();
                                year_obj.add(0, 1.0);
                                year_obj.add(1, (double) size);
                                year_obj.add(2, period);
                            } else {
                                year_obj.set(0, year_obj.get(0) + 1);
                                year_obj.set(1, year_obj.get(1) + size);
                                year_obj.set(2, year_obj.get(2) + period);
                            }
                            yearsCounts.put(year, year_obj);

                        }
                    }
                }

                TreeSet<Long> keyList = new TreeSet<>(yearsCounts.keySet());
                for (long key : keyList) {
                    int size = yearsCounts.get(key).get(0).intValue();
                    double avg_total = yearsCounts.get(key).get(1) / size;
                    double avg_period = yearsCounts.get(key).get(2) / size;
                    out.println(cowid + "," + key + "," + size + "," + avg_total + "," + avg_period);
                }

            }
            out.close();

            System.out.println("Done!! See result3.csv, for each year, each cow and each pixel calculate the average of the number of days this cow visit back to this pixel.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void printResult2() {
        File file = new File("result2.csv");

        if (file.exists()) {
            file.delete();
        }
        try (FileWriter fw = new FileWriter("result2.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {


            out.println("Cow_id, Years, Pixel_id, times of visiting back to pixel, period value,"+this.extra_title+"Northing,Easting");

            for (Map.Entry<String, HashMap<Long, HashMap<Long, HashSet<String>>>> cow_infos : yearInfos.entrySet()) {
                String cowid = cow_infos.getKey();
                for (Map.Entry<Long, HashMap<Long, HashSet<String>>> years_obj : cow_infos.getValue().entrySet()) {
                    long year = years_obj.getKey();
                    for (Map.Entry<Long, HashSet<String>> pixel_infos : years_obj.getValue().entrySet()) {
                        long pixel_id = pixel_infos.getKey();
                        int size = pixel_infos.getValue().size();
                        long diff = getDifferDate(pixel_infos.getValue());
                        String period = size - 1 != 0 ? String.valueOf((double) diff / (size - 1)) : "\\N";
//                        out.println(cowid + "," + year + "," + pixel_id + "," + size + "," + period);
                        String pixelVegClass = pixel_extra_info.get(pixel_id);
                        out.println(cowid + "," + year + "," + pixel_id + "," + size + "," + period + "," + pixelVegClass
                                + this.pixelList.get(pixel_id).getKey() + "," + this.pixelList.get(pixel_id).getValue());

                    }
                }

            }
            out.close();

            System.out.println("Done!! See result2.csv, for each year, each cow and each pixel calculate how many days this cow visit back to this pixel.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printResult1() {
        File file = new File("result1.csv");

        if (file.exists()) {
            file.delete();
        }

        try (FileWriter fw = new FileWriter("result1.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("Cow_id, Pixel_id, times of visiting back to pixel, Interval days,"+this.extra_title+"Northing,Easting");

            for (Map.Entry<String, HashMap<Long, HashSet<String>>> cow_infos : this.result.entrySet()) {
                String cowid = cow_infos.getKey();
                for (Map.Entry<Long, HashSet<String>> pixel_infos : cow_infos.getValue().entrySet()) {
                    long pixel_id = pixel_infos.getKey();
                    int size = pixel_infos.getValue().size();
                    long diff = getDifferDate(pixel_infos.getValue());
//                    out.println(cowid + "," + pixel_id + "," + size + "," + diff);

                    String pixelVegClass = this.pixel_extra_info.get(pixel_id);
                    out.println(cowid + "," + pixel_id + "," + size + "," + diff + "," + pixelVegClass
                            + "," + this.pixelList.get(pixel_id).getKey() + "," + this.pixelList.get(pixel_id).getValue());
                }
            }

            out.close();
            System.out.println("Done!! See result1.csv, for each cow and each pixel calculate how many days this cow visit back to this pixel.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getDifferDate(HashSet<String> value) {
        Date mindate = null, maxdate = null;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        for (String date : value) {
            try {
                Date tempdate = ft.parse(date);
                if (maxdate == null || tempdate.compareTo(maxdate) > 0) {
                    maxdate = tempdate;
                }

                if (mindate == null || tempdate.compareTo(mindate) < 0) {
                    mindate = tempdate;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

//        System.out.println((maxdate.getTime() - mindate.getTime()));
//        System.out.println((maxdate.getTime() - mindate.getTime()) / (1000*60*60*24));
//        System.out.println((double)(maxdate.getTime() - mindate.getTime()) / (1000*60*60*24));
        return Math.round((double) (maxdate.getTime() - mindate.getTime()) / (1000 * 60 * 60 * 24));

    }

    private void printVisitDateWithCowIDandPid(String cowid, String pid) {
        long Lpid = Long.parseLong(pid);
        HashSet<String> dataList = this.result.get(cowid).get(Lpid);
//        System.out.println(dataList.size());
        for (String d : dataList) {
            System.out.println(d);
        }
        System.out.println(getDifferDate(dataList));
    }

    private void printPixelListWihtCowID(String cowid) {
        System.out.println("==========");
        HashMap<Long, HashSet<String>> pList = this.result.get(cowid);
        System.out.println(cowid + "  " + pixelList.keySet().size());
        for (Long key : pList.keySet()) {
            HashSet<String> datelist = pList.get(key);
            System.out.println("    " + key);
            for (String dt : datelist) {
                System.out.println("        " + dt);
            }
        }
    }

    private void loadPixelData() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        int linenumber = 0;
        PData pd = new PData();

        try {
            br = new BufferedReader(new FileReader(this.pixelPath));
            String line = null;
            while ((line = br.readLine()) != null) {
                linenumber++;
                //jump the header
                if (linenumber == 1) {
                    //get extra title
                    String[] infos = line.split(",");
                    if (infos.length > 3) {
                        int i = 3;
                        for (; i < infos.length ; i++) {
                            this.extra_title += (infos[i] + ",");
                        }
                    }
                    continue;
                }
                String[] infos = line.split(",");
                Long pixelID = Long.parseLong(infos[0]);
                Double pixelNorthing = Double.parseDouble(infos[1]);
                Double pixelEasting = Double.parseDouble(infos[2]);

                //get extra information values
                String extraInfo = "";
                if (infos.length > 3) {
                    int i = 3;
                    for (; i < infos.length; i++) {
                        extraInfo += (infos[i] + ",");
                    }
                }


                this.pixelList.put(pixelID, new Pair<>(pixelNorthing, pixelEasting));
                this.pixel_extra_info.put(pixelID, extraInfo);
//                System.out.println(pixelID+","+pixelNorthing+","+pixelEasting+",");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Can not open the Pixel file, please check it. ");
        }
        System.out.println("read the pixel file done" + "   " + linenumber);
//        System.out.println(this.pixelList.size());
//        System.out.println(this.pixel_veg_class.size());

    }

    private void readGPSData() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        int linenumber = 0;
        PData pd = new PData();

        try {
            br = new BufferedReader(new FileReader(this.GPSData));
            String line = null;
            while ((line = br.readLine()) != null) {
                linenumber++;
                //jump the header
                if (linenumber == 1) {
                    continue;
                }

//                if(linenumber==10)
//                {
//                    break;
//                }

//                System.out.println(line);

                String[] infos = line.split(",");
                String gpsId = infos[0];
                String cowId = infos[1];
                String date = infos[2];
                double northing = Double.parseDouble(infos[3]);
                double easting = Double.parseDouble(infos[4]);
                long pixelId = getPixelID(northing, easting); //get the pixel that could include the current gps record


                //Store the visited information
                if (pixelId != -1) {
                    if (this.visited_result.containsKey(cowId)) {
                        HashMap<Long, Integer> pixelMapping = this.visited_result.get(cowId);
                        if (pixelMapping.containsKey(pixelId)) {
                            pixelMapping.put(pixelId, pixelMapping.get(pixelId) + 1);
                            visited_result.put(cowId, pixelMapping);
                        } else {
                            pixelMapping.put(pixelId, 1);
                            this.visited_result.put(cowId, pixelMapping);
                        }

                    } else {
                        HashMap<Long, Integer> pixelMapping = new HashMap<>();
                        pixelMapping.put(pixelId, 1);
                        this.visited_result.put(cowId, pixelMapping);
                    }
                }
                //System.out.println(visited_result.size());

                /*previous record is empty.
                  1. just read the second line.
                  2. Different cowid
                  3. Different date
                */
                if (cowId.equals(pd.cowId) && date.equals(pd.date)) // if the same cow but the GPS record is in the same day
                {
                    //calculate the speed of the cow start from the previous record
                    double distance = Math.abs(Math.sqrt(Math.pow(pd.easting - easting, 2) + Math.pow(pd.northing - northing, 2)));
                    double speed = distance / 5;
//                    System.out.println(Math.pow(pd.easting - easting, 2));
//                    System.out.println(Math.pow(pd.northing - northing, 2));
//                    System.out.println(pd.easting+" "+easting+" "+" "+pd.northing+" "+northing+" "+speed);

                    //if the speed need further processing
                    if (speed >= this.min_speed && speed <= this.max_speed) {
                        pixelId = getPixelID(northing, easting); //get the pixel that could include the current gps record
//                        System.out.println(linenumber+" "+pixelId);
//                        if (cowId.equals("1") && pixelId == 2) {
//                            System.out.println(Math.pow(pd.easting - easting, 2));
//                            System.out.println(Math.pow(pd.northing - northing, 2));
//                            System.out.println(pd.easting + " " + easting + " " + " " + pd.northing + " " + northing + " " + speed);
//                            System.out.println(linenumber + " " + date);
//                            System.out.println("=============");
//                        }

                        if (pixelId == -1) //if I can not find such a pixel
                        {
                            pd.setAttrs(infos);
                            continue;
                        }

                        if (this.result.containsKey(cowId)) {
                            HashMap<Long, HashSet<String>> pixelMapping = this.result.get(cowId);
                            if (pixelMapping.containsKey(pixelId)) {
                                HashSet<String> datelist = pixelMapping.get(pixelId);
                                if (!datelist.contains(date))
                                    datelist.add(date);
                            } else {
                                HashSet<String> datelist = new HashSet<>();
                                datelist.add(date);
                                pixelMapping.put(pixelId, datelist);
                                this.result.put(cowId, pixelMapping);
                            }

                        } else {
                            HashMap<Long, HashSet<String>> pixelMapping = new HashMap<>();
                            HashSet<String> datelist = new HashSet<>();
                            datelist.add(date);
                            pixelMapping.put(pixelId, datelist);
                            this.result.put(cowId, pixelMapping);
                        }
                    }
                }

                pd.setAttrs(infos);
            }
            br.close();
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("Can not open the Coordination file, please check it. ");
        }
        System.out.println("read the gps file done" + "   " + linenumber);
//        System.out.println(this.result.size());
    }

    private long getPixelID(double northing, double easting) {
        long result = -1;
        for (Map.Entry<Long, Pair<Double, Double>> e : this.pixelList.entrySet()) {
            double x = e.getValue().getKey(); //northing
            double y = e.getValue().getValue(); //easting
            //easting puls, norting sub
//            if ((northing < x && northing > x - this.range_size) && (easting > y && easting < y + this.range_size)) {
            if ((northing < x && northing > x - this.range_size) && (easting > y && easting < y + this.range_size)) {
                result = e.getKey();
                break;
            }
        }
//        System.out.println(result);
        return result;
    }


}
