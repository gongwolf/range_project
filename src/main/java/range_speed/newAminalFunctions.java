package range_speed;

import javafx.util.Pair;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class newAminalFunctions {

    String dataFile = "";
    String TimeFile = "";

    HashMap<String, HashMap<String, ArrayList<Double[]>>> pointsMap = new HashMap<>(); //cowid -> <date, List of points>

    HashMap<String, HashMap<String, HashSet<Pair<Double, Double>>>> pre_pointsMap = new HashMap<>(); //cowid -> <date, List of points>
    HashMap<String, HashMap<String, HashSet<Pair<Double, Double>>>> day_pointsMap = new HashMap<>(); //cowid -> <date, List of points>
    HashMap<String, HashMap<String, HashSet<Pair<Double, Double>>>> post_pointsMap = new HashMap<>(); //cowid -> <date, List of points>
    HashSet<String> dateList = new HashSet<>();
    HashSet<String> cowList = new HashSet<>();
    DateFormat TimeFormatter = new SimpleDateFormat("hh:mm:ss a");


    HashMap<String, Pair<String, String>> timeObj = new HashMap<>();
    ConvexHull ch = new ConvexHull();


    public newAminalFunctions(String fileDataPosition, String fileTime) {
        this.dataFile = fileDataPosition;
        this.TimeFile = fileTime;

//        System.out.println(this.dataFile);
//        System.out.println(this.TimeFile);
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
//        System.out.println("--------------------------------------------------");
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
                if (infos.length >= 5) {
                    String c_cowid = infos[0];
                    String c_date = infos[1];
                    String c_time = infos[2];
                    double c_northing = Double.valueOf(infos[3]);
                    double c_easting = Double.valueOf(infos[4]);

                    this.cowList.add(c_cowid);
                    this.dateList.add(c_date);

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

        BufferedWriter area_bw = null;
        FileWriter area_fw = null;
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {


            File file = new File("MCP_Results.csv");
            File area_file = new File("MCP_Area_Results.csv");
            if (file.exists()) {
                file.delete();
            }

            if (area_file.exists()) {
                area_file.delete();
            }

            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);


            area_fw = new FileWriter(area_file.getAbsoluteFile(), true);
            area_bw = new BufferedWriter(area_fw);

            area_bw.write("Cow_id,Date,Pre_MCP_area,Day_MCP_Area,Post_MCP_Area,All_Day_area\n");
            bw.write("Cow_id,Date,Daily_period,Easting,Northing\n");


            List<String> sorted_cow_List = new ArrayList(this.cowList);
            Collections.sort(sorted_cow_List, new comparatorStr());
            List<String> sorted_date_List = new ArrayList(this.dateList);
            Collections.sort(sorted_date_List, new comparatorDate());

            for (String cowid : sorted_cow_List) {
                for (String date : sorted_date_List) {
                    HashSet<Pair<Double, Double>> pre_points = this.pre_pointsMap.get(cowid).get(date);
                    HashSet<Pair<Double, Double>> day_points = this.day_pointsMap.get(cowid).get(date);
                    HashSet<Pair<Double, Double>> post_points = this.post_pointsMap.get(cowid).get(date);
                    HashSet<Pair<Double, Double>> allDay_points = new HashSet<>();

                    if (pre_points == null && day_points == null && post_points == null) {
                        continue;
                    }

//                System.out.println(date);
                    StringBuffer sb = new StringBuffer();
                    StringBuffer a_sb = new StringBuffer();

                    a_sb.append(cowid).append(",").append(date).append(",");

                    if (pre_points != null) {
                        allDay_points.addAll(pre_points);
                        Point[] convex_points = FindConvexHull(pre_points);
                        for (int i = 0; i < convex_points.length; i++) {
                            if (convex_points[i] != null) {
                                sb.append(cowid).append(",").append(date).append(",").append("Pre_MCP").append(",");
                                sb.append(convex_points[i].x).append(",");
                                sb.append(convex_points[i].y).append("\n");
                            }
                        }
                        a_sb.append(convex_area(convex_points)).append(",");
                    } else {
                        a_sb.append(",");
                    }

                    if (day_points != null) {
                        allDay_points.addAll(day_points);
                        Point[] convex_points = FindConvexHull(day_points);
                        for (int i = 0; i < convex_points.length; i++) {
                            if (convex_points[i] != null) {
                                sb.append(cowid).append(",").append(date).append(",").append("Day_MCP").append(",");
                                sb.append(convex_points[i].x).append(",");
                                sb.append(convex_points[i].y).append("\n");
                            }
                        }
                        a_sb.append(convex_area(convex_points)).append(",");
                    } else {
                        a_sb.append(",");
                    }

                    if (post_points != null) {
                        allDay_points.addAll(post_points);
                        Point[] convex_points = FindConvexHull(post_points);
                        for (int i = 0; i < convex_points.length; i++) {
                            if (convex_points[i] != null) {
                                sb.append(cowid).append(",").append(date).append(",").append("Post_MCP").append(",");
                                sb.append(convex_points[i].x).append(",");
                                sb.append(convex_points[i].y).append("\n");
                            }
                        }
                        a_sb.append(convex_area(convex_points)).append(",");

                    } else {
                        a_sb.append(",");
                    }

                    Point[] convex_points = FindConvexHull(allDay_points);
                    for (int i = 0; i < convex_points.length; i++) {
                        if (convex_points[i] != null) {
                            sb.append(cowid).append(",").append(date).append(",").append("All_Day_MCP").append(",");
                            sb.append(convex_points[i].x).append(",");
                            sb.append(convex_points[i].y).append("\n");
                        }
                    }
                    a_sb.append(convex_area(convex_points)).append("\n");

                    bw.write(sb.toString());
                    area_bw.write(a_sb.toString());
//                }
                }
//            break;
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

                if (area_bw != null)
                    area_bw.close();

                if (area_fw != null)
                    area_fw.close();

                System.out.println("Done!! See MCP_Results.csv for MCP of each cow for each day.");
                System.out.println("Done!! See MCP_Area_Results.csv for MCP of each cow for each day.");

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }

    /**
     * @param cur_date the string of the current time
     * @param Sunrise  the sunrise time of the date of the input time records
     * @param Sunset   the sunset time of the date of the input time records
     * @return the type of the current time, 0 is the pre day, 1 is the day time, 2 is the post day
     */
    //Todo: consider the equal situations, for example, one date&time is equals to the sunrise or sunset time of the day
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

    public Point[] FindConvexHull(HashSet<Pair<Double, Double>> points) {
        Point[] p = new Point[points.size()];
        int i = 0;
        for (Pair<Double, Double> dd : points) {
//            System.out.println(dd.getKey() + "," + dd.getValue());
            p[i] = new Point();
            p[i].x = dd.getKey(); // Read X coordinate
            p[i].y = dd.getValue(); // Read y coordinate
            i++;
        }


        Point[] hull = ch.convex_hull(p).clone();
//        double[] xlist = new double[hull.length];
//        double[] ylist = new double[hull.length];
//        for (i = 0; i < hull.length; i++) {
//            if (hull[i] != null) {
//                xlist[i] = hull[i].x;
//                ylist[i] = hull[i].y;
//            }
//        }

        return hull;
//        StringBuffer sb = new StringBuffer();
//        sb.append("[");
//        for (i = 0; i < hull.length; i++) {
//            sb.append(xlist[i]);
//            if (i != hull.length - 1)
//                sb.append(";");
//        }
//        sb.append("]|[");
//        for (i = 0; i < hull.length; i++) {
//            sb.append(ylist[i]);
//            if (i != hull.length - 1)
//                sb.append(";");
//        }
//        sb.append("]");
//        sb.append("plt.plot([");
//        for (i = 0; i < hull.length; i++) {
//            sb.append(xlist[i]);
//            if (i != hull.length - 1)
//                sb.append(",");
//        }
//        sb.append("],[");
//        for (i = 0; i < hull.length; i++) {
//            sb.append(ylist[i]);
//            if (i != hull.length - 1)
//                sb.append(",");
//        }
//        sb.append("],'ro')");
//
//        System.out.println(sb);
//        for (i = 0; i < hull.length - 1; i++) {
//            System.out.println("plt.plot([" + xlist[i] + "," + xlist[i + 1] + "],[" + ylist[i] + "," + ylist[i + 1] + "],color='r', linewidth=0.5)");
//        }
//
//        System.out.println("plt.plot([" + xlist[i] + "," + xlist[0] + "],[" + ylist[i] + "," + ylist[0] + "],color='r', linewidth=0.5)");
//        return sb.toString();
//        return "";
    }

    public double convex_area(Point[] pin_s) {
        double area = 0;
        for (int i = 0; i < pin_s.length; i++) {
            int i1 = i + 1;
            int i2 = i - 1;
            if (i1 == pin_s.length) {
                i1 = 0;
            }

            if (i2 == -1) {
                i2 = pin_s.length - 1;
            }
            area += pin_s[i].x * (pin_s[i1].y - pin_s[i2].y);
        }
        area /= 2.0;
        return area;
    }
}
