package range_speed;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Date;
import java.lang.Math;



public class AnimalProject
{
    private static String fileDataPosition= "data/DataWithPosition_sample.csv";
    private static String fileWeather="data/Weather.csv";
    private static String fileTime="data/Time.csv";
    private final static String outputDistSino = "data/DistanceandSiniosity.csv";
    private final static String outputAll = "data/CompleteProcessedData.csv";
    private static void fileNameProcess() throws IOException
    {
        InputStreamReader inp = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(inp);
        //Process position file names
        System.out.println("Enter position file name (Default: DataWithPosition_sample.csv): ");
        String str = in.readLine();
        if(str.trim().length()>0)
        {
            fileDataPosition = str;
        }
        //process weather file names
        System.out.println("Enter whether file name (Default: Weather.csv) : ");
        str = in.readLine();
        if(str.trim().length()>0)
        {
            fileWeather = str;
        }
        //process Time file names
        System.out.println("Enter time file Name (Default, Time.csv): ");
        str = in.readLine();
        if(str.trim().length()>0)
        {
            fileTime = str;
        }
    }
    private static void writeHeaderDistSino(FileWriter writerDistSino) throws IOException
    {
        writerDistSino.append("Trt");
        writerDistSino.append(',');
        writerDistSino.append("Year");
        writerDistSino.append(',');
        writerDistSino.append("Cow Id");
        writerDistSino.append(',');
        writerDistSino.append("Date");
        writerDistSino.append(',');
        writerDistSino.append("Total_dist");
        writerDistSino.append(',');
        writerDistSino.append("Day_dist");
        writerDistSino.append(',');
        writerDistSino.append("NPre_dist");
        writerDistSino.append(',');
        writerDistSino.append("NPost_dist");
        writerDistSino.append(',');
        writerDistSino.append("Total_Siniosity");
        writerDistSino.append(',');
        writerDistSino.append("Day_Siniosity");
        writerDistSino.append(',');
        writerDistSino.append("Pre_Siniosity");
        writerDistSino.append(',');
        writerDistSino.append("Post_Siniosity");
        writerDistSino.append(',');
        writerDistSino.append("All_Wood");
        writerDistSino.append(',');
        writerDistSino.append("Day_Wood");
        writerDistSino.append(',');
        writerDistSino.append("Pre_Wood");
        writerDistSino.append(',');
        writerDistSino.append("Post_Wood");
        writerDistSino.append('\n');
    }
    private static void writeHeaderAll(FileWriter writerAll) throws IOException
    {
        writerAll.append("Trt");
        writerAll.append(',');
        writerAll.append("Year");
        writerAll.append(',');
        writerAll.append("Cow Id");
        writerAll.append(',');
        writerAll.append("Date");
        writerAll.append(',');
        writerAll.append("Total_dist");
        writerAll.append(',');
        writerAll.append("Day_dist");
        writerAll.append(',');
        writerAll.append("NPre_dist");
        writerAll.append(',');
        writerAll.append("NPost_dist");
        writerAll.append(',');
        writerAll.append("Total_Siniosity");
        writerAll.append(',');
        writerAll.append("Day_Siniosity");
        writerAll.append(',');
        writerAll.append("Pre_Siniosity");
        writerAll.append(',');
        writerAll.append("Post_Siniosity");
        writerAll.append(',');
        writerAll.append("All_Wood");
        writerAll.append(',');
        writerAll.append("Day_Wood");
        writerAll.append(',');
        writerAll.append("Pre_Wood");
        writerAll.append(',');
        writerAll.append("Post_Wood");
        writerAll.append(',');
        writerAll.append("Cum_PPT_in");
        writerAll.append(',');
        writerAll.append("Act_PPT_in");
        writerAll.append(',');
        writerAll.append("Temp_c");
        writerAll.append(',');
        writerAll.append("Wind_Degree");
        writerAll.append(',');
        writerAll.append("Wind_mph");
        writerAll.append(',');
        writerAll.append("Lunar");
        writerAll.append('\n');
    }

    private static void writeDistSino(FileWriter writerDistSino,PositionData PositionData) throws IOException
    {
        writerDistSino.append(PositionData.Treatment);
        writerDistSino.append(',');
        writerDistSino.append(PositionData.Year);
        writerDistSino.append(',');
        writerDistSino.append(PositionData.cowidPrevious);
        writerDistSino.append(',');
        writerDistSino.append(PositionData.PreviousDate);
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.dist));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.day_dist));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.pre_dist));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.post_dist));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.tot_sin));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.day_sin));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.pre_sin));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.post_sin));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.TotalWoodLandArea));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.DayWoodLandArea));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.PreNightWoodLandArea));
        writerDistSino.append(',');
        writerDistSino.append(String.valueOf(PositionData.PostNightWoodLandArea));
    }
    private static void writerall(FileWriter writerAll,PositionData PositionData) throws IOException
    {
        writerAll.append(PositionData.Treatment);
        writerAll.append(',');
        writerAll.append(PositionData.Year);
        writerAll.append(',');
        writerAll.append(PositionData.cowidPrevious);
        writerAll.append(',');
        writerAll.append(PositionData.PreviousDate);
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.dist));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.day_dist));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.pre_dist));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.post_dist));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.tot_sin));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.day_sin));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.pre_sin));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.post_sin));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.TotalWoodLandArea));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.DayWoodLandArea));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.PreNightWoodLandArea));
        writerAll.append(',');
        writerAll.append(String.valueOf(PositionData.PostNightWoodLandArea));
    }
    private static void ReadWeather(PositionData PositionData,int lineNumber,FileWriter writerAll) throws IOException
    {
        int flag4=0,tokenNumber;
        String weatherline="";
        String storagestring="";
        BufferedReader brWeather = new BufferedReader( new FileReader(fileWeather));//Reading Weather File
        while( (weatherline = brWeather.readLine()) != null)
        {
            if(lineNumber==1)
                continue;
            StringTokenizer st = new StringTokenizer(weatherline, ",");
            tokenNumber=0;
            while(st.hasMoreTokens())
            {
                tokenNumber++;
                storagestring=st.nextToken();
                if(tokenNumber==1)
                {
                    if(!storagestring.equals(PositionData.PreviousDate))
                    {
                        while(st.hasMoreTokens())
                        {storagestring=st.nextToken();}
                    }
                    else
                    {
                        while(st.hasMoreTokens())
                        {
                            if(flag4==0)
                            {
                                writerall(writerAll,PositionData);//Writes the data to complete Processed file if date matches
                                flag4=1;
                            }
                            storagestring=st.nextToken();
                            writerAll.append(',');
                            writerAll.append(storagestring);
                        }
                    }
                }
            }
        }
        if(flag4!=0)
            writerAll.append('\n');
        brWeather.close();
    }
    private static void ReadTime(PositionData PositionData,int lineNumber,FileWriter writerAll) throws IOException, ParseException
    {
        DateFormat formatter1 ;
        formatter1 = new SimpleDateFormat("hh:mm:ss a");
        BufferedReader brTime = new BufferedReader( new FileReader(fileTime));//Reading time file!!!
        String timeline="";
        String storagestring="";
        while( (timeline = brTime.readLine()) != null)
        {
            if(lineNumber==1)
                continue;
            StringTokenizer st = new StringTokenizer(timeline, ",");
            while(st.hasMoreTokens())
            {
                storagestring=st.nextToken();
                if(!storagestring.equals(PositionData.PreviousDate))
                {
                    while(st.hasMoreTokens())
                    {
                        storagestring=st.nextToken();
                    }
                }
                else
                {
                    while(st.hasMoreTokens())
                    {
                        storagestring=st.nextToken();
                        Date sunrise=formatter1.parse(storagestring);
                        storagestring=st.nextToken();
                        Date sunset=formatter1.parse(storagestring);
                        if(PositionData.TimeCurrent.compareTo(sunset)==1)
                        {PositionData.flag2=3;PositionData.PostNightCount++;if(PositionData.Woodland==1)PositionData.PostNightWoodLandCount++;}
                        else if(PositionData.TimeCurrent.compareTo(sunrise)==-1 || PositionData.TimeCurrent.compareTo(sunrise)==0)
                        {PositionData.flag2=1;PositionData.PreNightCount++;if(PositionData.Woodland==1)PositionData.PreNightWoodLandCount++;}
                        else
                        {PositionData.flag2=2;PositionData.DayCount++;if(PositionData.Woodland==1)PositionData.DayWoodLandCount++;}
                    }
                }
            }
        }
        brTime.close();
    }
    private static void sinosityandwoodlandcalculations(PositionData PositionData)
    {
        PositionData.tot_sin=Math.sqrt(((PositionData.p2-PositionData.p1)*(PositionData.p2-PositionData.p1))+((PositionData.n2-PositionData.n1)*(PositionData.n2-PositionData.n1)));
        PositionData.tot_sin/=PositionData.dist;
        PositionData.day_sin=Math.sqrt(((PositionData.dd2-PositionData.dd1)*(PositionData.dd2-PositionData.dd1))+((PositionData.dn2-PositionData.dn1)*(PositionData.dn2-PositionData.dn1)));
        PositionData.day_sin/=PositionData.day_dist;
        PositionData.pre_sin=Math.sqrt(((PositionData.pd2-PositionData.pd1)*(PositionData.pd2-PositionData.pd1))+((PositionData.pn2-PositionData.pn1)*(PositionData.pn2-PositionData.pn1)));
        PositionData.pre_sin/=PositionData.pre_dist;
        PositionData.post_sin=Math.sqrt(((PositionData.p2-PositionData.sd1)*(PositionData.p2-PositionData.sd1))+((PositionData.n2-PositionData.sn1)*(PositionData.n2-PositionData.sn1)));
        PositionData.post_sin/=PositionData.post_dist;
        PositionData.TotalWoodLandArea=(PositionData.TotalWoodLandCount/PositionData.TotalNumberofWoodlandcolumn)*100;
        PositionData.DayWoodLandArea=(PositionData.DayWoodLandCount/PositionData.DayCount)*100;
        PositionData.PreNightWoodLandArea=(PositionData.PreNightWoodLandCount/PositionData.PreNightCount)*100;
        PositionData.PostNightWoodLandArea=(PositionData.PostNightWoodLandCount/PositionData.PostNightCount)*100;
    }
    private static void DistanceCalculations(PositionData PositionData)
    {
        //Calculating the distances

        double q= PositionData.x1-PositionData.x2;
        double q1=q*q;
        double r=PositionData.y1-PositionData.y2;
        double r1=r*r;
        r1=q1+r1;
        if(PositionData.dist==0)
        {
            PositionData.pd1=PositionData.p1=PositionData.x1;
            PositionData.pn1=PositionData.n1=PositionData.y1;
        }
        PositionData.dist+=Math.sqrt(r1);
        if(PositionData.flag2==1)
            PositionData.pre_dist+=Math.sqrt(r1);
        if(PositionData.flag2==2)
            PositionData.day_dist+=Math.sqrt(r1);
        if(PositionData.flag2==3)
            PositionData.post_dist+=Math.sqrt(r1);
        if(PositionData.DateDifference==2)
        {

            System.out.println("!!!!!"+PositionData.CurrentDate+"  "+PositionData.cowidCurrent);
            PositionData.x1=-1;
            PositionData.y1=-1;
            PositionData.DateDifference=0;
        }
        if(PositionData.day!=99 && PositionData.day_dist!=0)
        {
            PositionData.pd2=PositionData.x2;
            PositionData.pn2=PositionData.y2;
            PositionData.day=99;
            PositionData.dd1=PositionData.x2;
            PositionData.dn1=PositionData.y2;
        }
        if(PositionData.post!=99 && PositionData.post_dist!=0)
        {
            PositionData.dd2=PositionData.x2;
            PositionData.dn2=PositionData.y2;
            PositionData.post=99;
            PositionData.sd1=PositionData.x2;
            PositionData.sn1=PositionData.y2;
        }
    }
    private static void SwappingandloopIntialization(PositionData PositionData)
    {
        PositionData.TotalWoodLandCount=0;
        PositionData.PostNightWoodLandCount=PositionData.PreNightWoodLandCount=PositionData.DayWoodLandCount=0;
        PositionData.PostNightCount=PositionData.PreNightCount=PositionData.DayCount=0;
        PositionData.TotalNumberofWoodlandcolumn=0;
        PositionData.dist=0;
        PositionData.pre_dist=0;
        PositionData.day_dist=0;
        PositionData.post_dist=0;
        PositionData.flag2=99;
        PositionData.day=0;
        PositionData.post=0;
        PositionData.cowidPrevious=PositionData.cowidCurrent;
        PositionData.PreviousDate=PositionData.CurrentDate;
        PositionData.DatePrevious=PositionData.DateCurrent;
        PositionData.TimePrevious=PositionData.TimeCurrent;
        PositionData.PreviousTime=PositionData.CurrentTime;
    }

    public static void main(String[] args) throws IOException, ParseException
    {
        String storagestring=null;
        int lineNumber = 0, tokenNumber = 0,flag=0,flag1=0;
        StringTokenizer st = null;
        DateFormat formatter,formatter1 ;
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        formatter1 = new SimpleDateFormat("hh:mm:ss a");
        //1. get the input file names
        fileNameProcess();

        try
        {
            FileWriter writerDistSino = new FileWriter(outputDistSino);
            FileWriter writerAll = new FileWriter(outputAll);
            BufferedReader brPosition = new BufferedReader( new FileReader(fileDataPosition));
            //BufferedReader brTime = new BufferedReader( new FileReader(fileTime));
            //BufferedReader brWeather = new BufferedReader( new FileReader(fileWeather));
            PositionData PositionData=new PositionData();
            //2. write headers for both files
            writeHeaderDistSino(writerDistSino);
            writeHeaderAll(writerAll);
            //3. Reading Position FileDataPosition
            String positionline="";
            while( (positionline = brPosition.readLine()) != null)
            {
                lineNumber++;

                System.out.println(positionline);
                //if(lineNumber==10)
                //	break;
                //jump the title line of fileDataPosition file.
                if(lineNumber==1)
                    continue;
                // the linenumber of the first line of data is 2.
                tokenNumber=0;
                //read the splited information
                st = new StringTokenizer(positionline, ",");
                //System.out.println(positionline);
                while(st.hasMoreTokens())
                {
                    tokenNumber++;
                    storagestring=st.nextToken();
                    if(tokenNumber==1)//Token Number is Each Column
                    {
                        if(lineNumber==2)//lineNumber=2 implies the first data row
                        {
                            PositionData.cowidPrevious=storagestring;
                            PositionData.cowidCurrent=storagestring;
                        }
                        else//Else case is from the next rows
                        {
                            if(!storagestring.equals(PositionData.cowidPrevious))
                            {
                                flag=1;
                                flag1=1;
                                PositionData.cowidCurrent=storagestring;
                            }
                        }
                    }
                    if(tokenNumber==2)
                    {
                        if(lineNumber==2)
                        {
                            PositionData.PreviousDate=storagestring;
                            PositionData.CurrentDate=storagestring;
                            PositionData.DatePrevious = (java.util.Date)formatter.parse(storagestring);
                            PositionData.DateCurrent= (java.util.Date)formatter.parse(storagestring);
                        }
                        else
                        {
                            //if this recored's date is different from previous row.
                            if(!storagestring.equals(PositionData.PreviousDate))
                            {
                                flag=1;
                                flag1=1;
                                PositionData.CurrentDate=storagestring;
                                PositionData.DateCurrent = (java.util.Date)formatter.parse(storagestring);
                                PositionData.Year=storagestring.substring((storagestring.length()-4),storagestring.length());
                                //System.out.println(PositionData.Year);

                                if(Integer.parseInt(PositionData.Year)==2004 ||Integer.parseInt(PositionData.Year)==2005)
                                    PositionData.Treatment="Heavy";//Based on Year Treatment is decided
                                else
                                    PositionData.Treatment="light";

                                PositionData.DateDifference=Math.abs((PositionData.DatePrevious.getTime()-PositionData.DateCurrent.getTime())/(1000*60*60*24));
                                System.out.println(PositionData.DateDifference);
                            }
                        }
                    }
                    if(tokenNumber==3)
                    {
                        if(lineNumber==2)
                        {
                            PositionData.PreviousTime=storagestring;
                            PositionData.TimePrevious=formatter1.parse(storagestring);
                        }
                        else
                        {
                            PositionData.CurrentTime=storagestring;
                            PositionData.TimeCurrent=formatter1.parse(storagestring);
                        }
                    }
                    if(tokenNumber==4)
                    {
                        if(lineNumber==2)
                        {
                            PositionData.x1=new Double(storagestring);
                        }
                        else
                            PositionData.x2=new Double(storagestring);
                    }
                    if(tokenNumber==5)
                    {
                        if(lineNumber==2)
                        {
                            PositionData.y1=new Double(storagestring);
                        }
                        else
                            PositionData.y2=new Double(storagestring);
                    }
                    if(tokenNumber==6)
                    {
                        PositionData.Woodland=new Double(storagestring);
                        if(PositionData.Woodland==1)
                            PositionData.TotalWoodLandCount++;
                        PositionData.TotalNumberofWoodlandcolumn++;
                    }
                    if(PositionData.DateDifference==1)
                    {
                        PositionData.DateDifference=2;
                        flag=0;
                    }
                    else if(flag==1)
                    {
                        flag=0;
                        PositionData.x1=-1;
                        PositionData.y1=-1;
                    }
                }


                if(lineNumber>2)
                {
                    ReadTime(PositionData,lineNumber,writerAll);//Reading Time File!!
                    if(PositionData.x1!=-1)
                        if(flag!=1)
                            DistanceCalculations(PositionData);//Calculation of Distances!!
                    PositionData.x1=PositionData.x2;
                    PositionData.y1=PositionData.y2;
                }


                if(flag1==1)
                {
                    PositionData.p2=PositionData.x2;
                    PositionData.n2=PositionData.y2;
                    sinosityandwoodlandcalculations(PositionData);//Calculating sinosity and woodland
                    writeDistSino(writerDistSino,PositionData);//Writing into file after Calculations
                    ReadWeather(PositionData,lineNumber,writerAll);//Reading Weather File!!
                    writerDistSino.append('\n');
                    flag1=0;
                    SwappingandloopIntialization(PositionData);//swapping Current and Previous
                }
                tokenNumber = 0;
//                System.out.println(PositionData);
            }

            writeDistSino(writerDistSino,PositionData);//Writing Values
            writerDistSino.append('\n');
            System.out.println("Done!! See ' DistanceandSiniosity file' for whole Data with All Calculations");
            ReadWeather(PositionData,lineNumber,writerAll);//Reading Weather File and attaching weather Factors
            writerDistSino.flush();
            writerDistSino.close();
            writerAll.flush();
            writerAll.close();
            System.out.println("Done!! See 'Complete Processed Data file' for whole Data with Weather Factors");
        }//Try Ending
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

