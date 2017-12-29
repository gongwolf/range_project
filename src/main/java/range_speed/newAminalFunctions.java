package range_speed;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class newAminalFunctions {

    String dataFile = "";
    String TimeFile = "";

    HashMap<String, HashMap<String, ArrayList<Double[]>>> pointsMap = new HashMap<>(); //cowid -> <date, List of points>

    HashMap<String, HashMap<String, HashSet<Pair<Double,Double>>>> pre_pointsMap = new HashMap<>(); //cowid -> <date, List of points>
    HashMap<String, HashMap<String, HashSet<Pair<Double,Double>>>> day_pointsMap = new HashMap<>(); //cowid -> <date, List of points>
    HashMap<String, HashMap<String, HashSet<Pair<Double,Double>>>> post_pointsMap = new HashMap<>(); //cowid -> <date, List of points>
    DateFormat TimeFormatter = new SimpleDateFormat("hh:mm:ss a");


    HashMap<String, Pair<String, String>> timeObj = new HashMap<>();


    public newAminalFunctions(String fileDataPosition, String fileTime) {
        this.dataFile = fileDataPosition;
        this.TimeFile = fileTime;

        System.out.println(this.dataFile);
        System.out.println(this.TimeFile);
        readTime();
        readGPSData();


    }

    private void readTime() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        int linenumber = 0;
        try {
            br = new BufferedReader(new FileReader(this.TimeFile));
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

    private void readGPSData() {
        BufferedReader br = null;
        int linenumber = 0;

        try {
            br = new BufferedReader(new FileReader(this.dataFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                linenumber++;
                //jump the header
                if (linenumber == 1) {
                    continue;
                }

                String[] infos = line.split(",");
                if (infos.length >= 5 && infos[0].equals("8833") && infos[1].equals("4/6/2006")) {
                    String c_cowid = infos[0];
                    String c_date = infos[1];
                    String c_time = infos[2];
                    double c_northing = Double.valueOf(infos[3]);
                    double c_easting = Double.valueOf(infos[4]);

//                    System.out.println(linenumber+","+c_cowid + " , " + c_date + " , " + c_time + " , " + c_northing + " , " + c_easting);

                    String sun_rise = timeObj.get(c_date).getKey();
                    String sun_set = timeObj.get(c_date).getValue();
                    int ptype = getPartitionType(c_time, sun_rise, sun_set);
//                    System.out.println(sun_rise+"  "+sun_set+" "+getPartitionType(c_time,sun_rise,sun_set));

                    if (ptype == 0) {
//                        System.out.println(linenumber+","+c_cowid + " , " + c_date + " , " + c_time + " , " + c_northing + " , " + c_easting);
//                        System.out.println(sun_rise+"  "+sun_set+" "+getPartitionType(c_time,sun_rise,sun_set));
                        if (this.pre_pointsMap.containsKey(c_cowid)) {
                            HashMap<String, HashSet<Pair<Double, Double>>> d = pre_pointsMap.get(c_cowid);
                            if (d.containsKey(c_date)) {
                                HashSet<Pair<Double, Double>> pList = d.get(c_date);
                                pList.add(new Pair<>(c_easting, c_northing));
                            } else {
                                HashSet<Pair<Double, Double>> pList = new HashSet<>();
                                pList.add(new Pair<>(c_easting, c_northing));
                                d.put(c_date, pList);
                            }
                        } else {
                            HashMap<String, HashSet<Pair<Double, Double>>> d = new HashMap<>();
                            HashSet<Pair<Double, Double>> pList = new HashSet<>();
                            pList.add(new Pair<>(c_easting, c_northing));
                            d.put(c_date, pList);
                            this.pre_pointsMap.put(c_cowid, d);
                        }
                    } else if (ptype == 1) {
                        if (this.day_pointsMap.containsKey(c_cowid)) {
                            HashMap<String, HashSet<Pair<Double, Double>>> d = day_pointsMap.get(c_cowid);
                            if (d.containsKey(c_date)) {
                                HashSet<Pair<Double, Double>> pList = d.get(c_date);
                                pList.add(new Pair<>(c_easting, c_northing));
                            } else {
                                HashSet<Pair<Double, Double>> pList = new HashSet<>();
                                pList.add(new Pair<>(c_easting, c_northing));
                                d.put(c_date, pList);
                            }
                        } else {
                            HashMap<String, HashSet<Pair<Double, Double>>> d = new HashMap<>();
                            HashSet<Pair<Double, Double>> pList = new HashSet<>();
                            pList.add(new Pair<>(c_easting, c_northing));
                            d.put(c_date, pList);
                            this.day_pointsMap.put(c_cowid, d);
                        }

                    } else if (ptype == 2) {
                        if (this.post_pointsMap.containsKey(c_cowid)) {
                            HashMap<String, HashSet<Pair<Double, Double>>> d = post_pointsMap.get(c_cowid);
                            if (d.containsKey(c_date)) {
                                HashSet<Pair<Double, Double>> pList = d.get(c_date);
                                pList.add(new Pair<>(c_easting, c_northing));
                            } else {
                                HashSet<Pair<Double, Double>> pList = new HashSet<>();
                                pList.add(new Pair<>(c_easting, c_northing));
                                d.put(c_date, pList);
                            }
                        } else {
                            HashMap<String, HashSet<Pair<Double, Double>>> d = new HashMap<>();
                            HashSet<Pair<Double, Double>> pList = new HashSet<>();
                            pList.add(new Pair<>(c_easting, c_northing));
                            d.put(c_date, pList);
                            this.post_pointsMap.put(c_cowid, d);
                        }

                    }


                } else {
//                    System.out.println(linenumber + ":" + line);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("read the gps file done" + "   " + linenumber);
    }


    public void convexHull() {
        HashMap<String, HashSet<Pair<Double, Double>>> d = this.post_pointsMap.get("8833");
        HashSet<Pair<Double, Double>> points = d.get("4/6/2006");
        int linenumber = 0;
        for(Pair<Double, Double> dd: points)
        {
            System.out.println(linenumber++ +"  "+dd.getKey()+"  "+dd.getValue());
        }

    }

    /**
     * @param cur_date the string of the current time
     * @param Sunrise  the sunrise time of the date of the input time records
     * @param Sunset   the sunset time of the date of the input time records
     * @return the type of the current time, 0 is the pre day, 1 is the day time, 2 is the post day
     */
    public int getPartitionType(String cur_date, String Sunrise, String Sunset) {
        Date Sunrise_D = null;
        Date Sunset_D = null;
        try {
            Sunrise_D = this.TimeFormatter.parse(Sunrise);
            Sunset_D = this.TimeFormatter.parse(Sunset);

            Date other = this.TimeFormatter.parse(cur_date);

            if (other.before(Sunrise_D)) {
                return 0;
            } else if (other.after(Sunset_D)) {
                return 2;
            } else {
                return 1;
            }
        } catch (ParseException e) {
            System.out.println("There is something wrong with your time formation, please check it");
            System.exit(0);
        }

        return -1;
    }
}
