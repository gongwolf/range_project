package range_pixel;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.text.*;

public class range_pixel {
    String pixelPath = "data/pixel/pixel_1.csv";
    //    String GPSData = "data/pixel/sample_gps.csv";
    String GPSData = "data/pixel/cordinate_points_1.csv";
    HashMap<Long, Pair<Double, Double>> pixelList = new HashMap<Long, Pair<Double, Double>>(); //pixel_id -> <northing,easting>
    HashMap<String, HashMap<Long, HashSet<String>>> result = new HashMap<String, HashMap<Long, HashSet<String>>>();
    HashMap<String, HashMap<Long, HashMap<Long, HashSet<String>>>> yearInfos = new HashMap<>();

    public static void main(String args[]) {
        range_pixel rp = new range_pixel();
        rp.loadPixelData();
//        System.out.println(rp.pixelList.size());
        rp.readGPSData();
//        System.out.println(rp.result.size());
//        rp.printPixelListWihtCowID("14492");

        rp.printResult1();
//        rp.setYearinfos();
//        rp.printResult3();
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


    private void printResult3() {
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
                double avg_total = yearsCounts.get(key).get(1)/size;
                double avg_period = yearsCounts.get(key).get(2)/size;
                System.out.println(cowid + " " + key + " " + size  + " " + avg_total + " " + avg_period);
            }

        }

    }


    private void printResult2() {
        for (Map.Entry<String, HashMap<Long, HashMap<Long, HashSet<String>>>> cow_infos : yearInfos.entrySet()) {
            String cowid = cow_infos.getKey();
            for (Map.Entry<Long, HashMap<Long, HashSet<String>>> years_obj : cow_infos.getValue().entrySet()) {
                long year = years_obj.getKey();
                for (Map.Entry<Long, HashSet<String>> pixel_infos : years_obj.getValue().entrySet()) {
                    long pixel_id = pixel_infos.getKey();
                    int size = pixel_infos.getValue().size();
                    long diff = getDifferDate(pixel_infos.getValue());
                    String period = size - 1 != 0 ? String.valueOf((double) diff / (size - 1)) : "\\N";
                    System.out.println(cowid + " " + year + " " + pixel_id + " " + size + " " + period);

                }
            }

        }
    }

    private void printResult1() {
        for (Map.Entry<String, HashMap<Long, HashSet<String>>> cow_infos : this.result.entrySet()) {
            String cowid = cow_infos.getKey();
            for (Map.Entry<Long, HashSet<String>> pixel_infos : cow_infos.getValue().entrySet()) {
                long pixel_id = pixel_infos.getKey();
                int size = pixel_infos.getValue().size();
                long diff = getDifferDate(pixel_infos.getValue());
                System.out.println(cowid + " " + pixel_id + " " + size + " " + diff);

            }

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

        return (maxdate.getTime() - mindate.getTime()) / (24 * 60 * 60 * 1000);

    }

    private void printVisitDateWithCowIDandPid(String cowid, String pid) {
        long Lpid = Long.parseLong(pid);
        HashSet<String> dataList = this.result.get(cowid).get(Lpid);
//        System.out.println(dataList.size());
        for (String d : dataList) {
            System.out.println(d);
        }
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
                    continue;
                }
                String[] infos = line.split("\\t");
                Long pixelID = Long.parseLong(infos[0]);
                Double pixelNorthing = Double.parseDouble(infos[1]);
                Double pixelEasting = Double.parseDouble(infos[2]);
                this.pixelList.put(pixelID, new Pair<Double, Double>(pixelNorthing, pixelEasting));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("read the pixel file done" + "   " + linenumber);

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

//                System.out.println(linenumber);

                String[] infos = line.split(",");
                String gpsId = infos[0];
                String cowId = infos[1];
                String date = infos[2];
                double northing = Double.parseDouble(infos[3]);
                double easting = Double.parseDouble(infos[4]);

                /*previous record is empty.
                  1. just read the second line.
                  2. Different cowid
                  3. Different date
                */
                if (cowId.equals(pd.cowId) && date.equals(pd.date)) // if the same cow but the GPS record is in a different day
                {
                    //calculate the speed of the cow start from the previous record
                    double distance = Math.abs(Math.sqrt(Math.pow(pd.easting - easting, 2) + Math.pow(pd.northing - northing, 2)));
                    double speed = distance / 5;
//                    System.out.println(Math.pow(pd.easting - easting, 2));
//                    System.out.println(Math.pow(pd.northing - northing, 2));
//                    System.out.println(pd.easting+" "+easting+" "+" "+pd.northing+" "+northing+" "+speed);

                    //if the speed need further processing
                    if (speed >= 5 && speed <= 100) {
                        //Todo: may it could find multiple pixel
                        long pixelId = getPixelID(northing, easting); //get the pixel that could include the current gps record
//                        if (cowId.equals("14492") && pixelId == 1) {
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
            e.printStackTrace();
        }
        System.out.println("read the gps file done" + "   " + linenumber);
    }

    private long getPixelID(double northing, double easting) {
        long result = -1;
        for (Map.Entry<Long, Pair<Double, Double>> e : this.pixelList.entrySet()) {
            double y = e.getValue().getKey(); //northing
            double x = e.getValue().getValue(); //easting
            //easting puls, norting sub
            if ((northing < x && northing > x - 30) && (easting > y && easting < y + 30)) {
                result = e.getKey();
                break;
            }
        }
//        System.out.println(result);
        return result;
    }


}
