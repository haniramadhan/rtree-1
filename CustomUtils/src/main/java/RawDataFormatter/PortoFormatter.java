package RawDataFormatter;

import gnu.trove.list.array.TIntArrayList;

import java.io.*;

public class PortoFormatter {

    public static void main(String[] args){
        File rawFile = new File("../dataset/train.csv");
        File formattedFile = new File("../dataset/porto.csv");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(rawFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String input;

            fos = new FileOutputStream(formattedFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));

            boolean start  =true;
            int p = 0;
            while ((input = bufferedReader.readLine()) != null) {
                if(start) {
                    start = false;
                    continue;
                }
                String[] tokenized = input.split(",");
                String toWrite = "";
                if(tokenized.length < 10)
                    continue;
                else
                    p++;
                for(int i=8;i<tokenized.length;i+=2){
                    int subLeft = 1, subRight = 1;
                    if (i == 8 )
                        subLeft = 3;
                    if(i==tokenized.length-2)
                        subRight = 3;

                    String latStr = tokenized[i].substring(subLeft);
                    String longStr= tokenized[i+1].substring(0,tokenized[i+1].length()-subRight);
                    Double lat = Double.parseDouble(latStr), lon = Double.parseDouble(longStr);
                    if(!((40 <= lon && lon <= 42) && (-10.2 <= lat && lat <= -7.8)))
                        continue;

                    toWrite = toWrite + String.format("%.3f",lat) + "," +   String.format("%.3f",lon);
                    if(i < tokenized.length-2)
                        toWrite = toWrite + ";";

                }
                if(toWrite.length()<100)
                    continue;
                if(toWrite.charAt(toWrite.length()-1) == ';')
                    toWrite = toWrite.substring(0,toWrite.length()-1);
                if(p%1000==0)
                    System.out.println(p);
                bufferedWriter.write(toWrite);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.close();
            fos.close();
            bufferedReader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
