package range_pixel;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class range_pixel {
    String pixelPath = "data/pixel/pixel.csv";
    //    String GPSData = "data/pixel/sample_gps.csv";
    String GPSData = "data/pixel/cordinate_points.csv";
    HashMap<Long, Pair<Double, Double>> pixelList = new HashMap<Long, Pair<Double, Double>>(); //pixel_id -> <northing,easting>
    HashMap<String, HashMap<Long, HashSet<String>>> result = new HashMap<String, HashMap<Long, HashSet<String>>>();

    public static void main(String args[]) {
        range_pixel rp = new range_pixel();
        rp.loadPixelData();
        System.out.println(rp.pixelList.size());
        rp.readGPSData();
        System.out.println(rp.result.size());
        rp.printPixelListWihtCowID("10");
//        rp.printVisitDateWithCowIDandPid("1290","61");

    }

    private void printVisitDateWithCowIDandPid(String cowid, String pid) {
        long Lpid = Long.parseLong(pid);
        HashSet<String> dataList = this.result.get(cowid).get(Lpid);
//        System.out.println(dataList.size());
        for(String d:dataList)
        {
            System.out.println(d);
        }
    }

    private void printPixelListWihtCowID(String cowid) {
        System.out.println("==========");
        HashMap<Long, HashSet<String>> pList = this.result.get(cowid);
        System.out.println(cowid+"  "+pixelList.keySet().size());
        for(Long key:pList.keySet())
        {
            HashSet<String> datelist = pList.get(key);
            System.out.println("    "+key);
            for(String dt:datelist)
            {
                System.out.println("        "+dt);
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
                String[] infos = line.split("\t");
                Long pixelID = Long.parseLong(infos[0]);
                Double pixelEasting = Double.parseDouble(infos[1]);
                Double pixelNorthing = Double.parseDouble(infos[2]);
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
                if (pd.isnull()) { //if it is the first record I read
                    pd.setNull(false);
                } else if (!pd.cowId.equals(cowId)) { // if it is not the second record and the record match a different cow
                } else if (!pd.date.equals(date)) { // if the same cow but the GPS record is in a different day
                } else {
                    //calculate the speed of the cow start from the previous record
                    double distance = Math.abs(Math.sqrt(Math.pow(pd.easting - easting, 2) + Math.pow(pd.northing - northing, 2)));
                    double speed = distance / 5;

                    //if the speed need further processing
                    if (speed >= 5 && speed <= 100) {
                        //Todo: may it could find multiple pixel
                        long pixelId = getPixelID(northing, easting); //get the pixel that could include the current gps record

                        if(pixelId==-1) //if I can not find such a pixel
                        {
                            continue;
                        }

                        if (this.result.containsKey(cowId)) {
                            HashMap<Long, HashSet<String>> pixelMapping = this.result.get(cowId);
                            if (pixelMapping.containsKey(pixelId)) {
                                HashSet<String> datelist = pixelMapping.get(pixelId);
                                if (!datelist.contains(date))
                                    datelist.add(date);
                            }else
                            {
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
            double x = e.getValue().getKey(); //northing
            double y = e.getValue().getValue(); //easting
            //easting puls, norting sub
            if ((northing < x && northing > x - 30) && (easting > y && easting < y + 30)) {
                result = e.getKey();
                break;
            }
        }
        return result;
    }


}
