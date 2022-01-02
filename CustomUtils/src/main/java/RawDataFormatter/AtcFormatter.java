package RawDataFormatter;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AtcFormatter {

    private static TLongArrayList lastTimestampSecond;
    private static TLongIntHashMap users;
    private static List<List<double[]>> buffer;
    private static List<List<double[]>> trajectory;


    public static void main(String[] args){

        String[] filenames = {
                "20121024","20121028","20121031","20121104","20121107","20121111","20121114","20121118",
                "20121121","20121125","20121128","20121202","20121205","20121209","20121216","20121219",
                "20121223","20121226","20130106","20130109","20130113","20130116","20130120","20130123",
                "20130127","20130130","20130203","20130206","20130210","20130213","20130217","20130220",
                "20130224","20130227","20130303","20130306","20130310","20130317","20130320","20130324",
                "20130327","20130331","20130403","20130407","20130410","20130414","20130417","20130421",
                "20130424","20130428","20130501","20130505","20130508","20130512","20130515","20130519",
                "20130522","20130526","20130529","20130602","20130605","20130609","20130612","20130616",
                "20130619","20130623","20130626","20130630","20130703","20130707","20130710","20130717",
                "20130721","20130724","20130728","20130807","20130811","20130814","20130818","20130821",
                "20130825","20130828","20130901","20130904","20130911","20130915","20130918","20130922",
                "20130925","20130929"
        };
        File rawFolder = new File("../dataset/atc/atc-");
        File formattedFile = new File("../dataset/atc-x-v2.csv");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;



        int p = 0;
        try {
            fos = new FileOutputStream(formattedFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
            for (int d = 0; d <filenames.length; d++) {

                int iUser = 0;
                lastTimestampSecond = new TLongArrayList();
                users = new TLongIntHashMap();
                buffer = new ArrayList<>();
                trajectory = new ArrayList<>();
                
                int iLine = 0;
                fis = new FileInputStream(rawFolder + filenames[d] +".csv");
                bufferedReader = new BufferedReader(new InputStreamReader(fis));
                String input;


                String toWrite = "";

                while ((input = bufferedReader.readLine()) != null) {
                    iLine++;
                    if(iLine % 50000 == 0)
                        System.out.println(d+"/"+filenames.length+":"+iLine);
                    String[] tokenized = input.split(",");
                    long curTimestamp = (long) Math.floor(Double.parseDouble(tokenized[0]));
                    long userId = Long.parseLong(tokenized[1]);
                    double[] values = new double[6];
                    for(int i=0;i<values.length;i++){
                        values[i] = Double.parseDouble(tokenized[2+i]);
                    }

                    if(users.containsKey(userId)){
                        //always add to buffer
                        int iiuser = users.get(userId);
                        if(curTimestamp - lastTimestampSecond.get(iiuser) > 3){
                            //add to trajectory
                            addToTrajectory(iiuser);
                            lastTimestampSecond.set(iiuser,curTimestamp);
                            buffer.set(iiuser,new ArrayList<>());

                            //renew trajectory when the time between current record  and previous record is larger than a threshold
                            if(curTimestamp - lastTimestampSecond.get(iiuser) > 10) {
                                if (trajectory.get(iiuser).size() > 5){
                                    writeToFile(iiuser,bufferedWriter);
                                    p++;
                                    if (p % 1000 == 0)
                                        System.out.println(d+"/"+filenames.length+":"+p);
                                }
                                trajectory.set(iiuser,new ArrayList<>());
                            }
                        }
                        buffer.get(iiuser).add(values);
                    }
                    else {
                        users.put(userId, iUser);
                        lastTimestampSecond.add(curTimestamp);
                        buffer.add(new ArrayList<>());
                        buffer.get(iUser).add(values);
                        trajectory.add(new ArrayList<>());
                        iUser++;
                    }
                }
                for(int ii=0;ii<users.values().length;ii++) {
                    int iiuser = users.values()[ii];
                    if(ii == users.values().length-1)
                        continue;
                    addToTrajectory(iiuser);
                    if (trajectory.get(iiuser).size() > 10){
                       writeToFile(iiuser,bufferedWriter);
                        p++;
                        if (p % 1000 == 0)
                            System.out.println(d+"/"+filenames.length+":"+p);
                    }
                    trajectory.set(iiuser,new ArrayList<>());
                }
                bufferedReader.close();
                fis.close();
            }
            bufferedWriter.close();
            fos.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void addToTrajectory(int iiuser){
        double[] sumVal = new double[6];
        for(int j=0;j<buffer.get(iiuser).size();j++) {
            for (int i = 0; i < sumVal.length; i++){
                sumVal[i] = sumVal[i] + buffer.get(iiuser).get(j)[i];
            }
        }
        for (int i = 0; i < sumVal.length; i++){
            sumVal[i] = sumVal[i] / buffer.get(iiuser).size();
        }
        trajectory.get(iiuser).add(sumVal);
    }

    public static String extractTrajectory(int iiuser){
        String toWrite = "";
        for(int j=0;j<trajectory.get(iiuser).size();j++) {
            double[] point = trajectory.get(iiuser).get(j);
            for (int i = 0; i < point.length; i++){
                toWrite = toWrite + String.format("%.5f",point[i]);
                if(i<point.length-1)
                    toWrite = toWrite + ",";
            }
            if(j<trajectory.get(iiuser).size()-1)
                toWrite = toWrite + ";";
        }
        return toWrite;
    }

    public static void writeToFile(int iiuser, BufferedWriter bufferedWriter) throws IOException {
        String toWrite = extractTrajectory(iiuser);
        bufferedWriter.write(toWrite);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}
