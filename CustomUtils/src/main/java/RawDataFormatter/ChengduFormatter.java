package RawDataFormatter;

import java.io.*;

public class ChengduFormatter {
    public static void main(String[] args){
        File rawFile = new File("../dataset/gps_201611");
        File formattedFile = new File("../dataset/chengdu_new_y.csv");
        System.out.println(System.getProperty("user.dir"));
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        int q = 0;
        int minFile = 1;
        int maxFile =12;
        try {
            fos = new FileOutputStream(formattedFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
            for (int d = minFile; d <= maxFile; d++) {
                String num = String.valueOf(d);
                if (d < 10)
                    num = "0" + d;
                fis = new FileInputStream(rawFile + num);
                bufferedReader = new BufferedReader(new InputStreamReader(fis));
                String input;

                int p=0;
                int numPoints = 1;

                String order = "";
                String toWrite = "";
                while ((input = bufferedReader.readLine()) != null) {
                    String[] tokenized = input.split(",");

                    if (!tokenized[1].equals(order)) { //if new ID is identified, write buffer of previous reading
                        if(numPoints > 20 && numPoints < 370)
                            if (p > 0) {
                                bufferedWriter.write(toWrite);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                        p++;

                        if (p % 100000 == 0)
                            System.out.println(p);
                        order = tokenized[1];
                        toWrite = tokenized[3] + "," + tokenized[4];
                        numPoints = 1;
                    } else {
                        toWrite = toWrite + ";" + tokenized[3] + "," + tokenized[4];
                        numPoints++;
                    }
                }
                System.out.println(num+":"+p);
                bufferedWriter.write(toWrite);
                bufferedWriter.newLine();
                bufferedWriter.flush();
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
}
