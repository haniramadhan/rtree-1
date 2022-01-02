package RawDataFormatter;

import main.java.CustomUtils.TrajUtil;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrajDataVerifier {

    static String trajFileInString = "../dataset/chengdu_new_y.csv";
    static String trajFileOutString = "../dataset/chengdu_new_y-out.csv";
    public static void main(String[] args){
        File formattedFile = new File(trajFileInString);
        TrajUtil.loadTrajDatasetFromFile(formattedFile.getPath());
        long lengthSum = 0;
        TIntIntHashMap lengthMap = new TIntIntHashMap();
        for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
            int length =TrajUtil.trajectoryDataset.get(i).size();
            lengthSum +=length;
            if(lengthMap.get(length)!=0)
                lengthMap.put(length,lengthMap.get(length)+1);
            else
                lengthMap.put(length,1);
        }

        System.out.println(lengthSum/TrajUtil.trajectoryDataset.size());
        for(int i=0;i<1000;i++){
            if(lengthMap.get(i)!=-1||lengthMap.get(i)!=0)
                System.out.println(i+":"+lengthMap.get(i));
        }

         filter(147);
    }

    public static void filter(double averageLimit){
        long lengthSum = 0;
        int max = 0;
        for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
            int length =TrajUtil.trajectoryDataset.get(i).size();
            if(max<length)
                max = length;
            lengthSum +=length;
        }
        List<List<double[]>> filteredDataset = new ArrayList<>();
        double averageLength = lengthSum*1.0/TrajUtil.trajectoryDataset.size();
        System.out.println(averageLength);
        if(!(averageLength<averageLimit))
        while(averageLength < averageLimit-1 || averageLength > averageLimit+1 ){
            lengthSum = 0;
            filteredDataset = new ArrayList<>();
            for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
                int length =TrajUtil.trajectoryDataset.get(i).size();
                if(length!=max){
                    filteredDataset.add(TrajUtil.trajectoryDataset.get(i));
                    lengthSum+=length;
                }
            }

            TrajUtil.trajectoryDataset = filteredDataset;
            averageLength = lengthSum*1.0/TrajUtil.trajectoryDataset.size();
            System.out.println(max+" "+averageLength);
            max--;
        }
        rewrite();
    }
    public static void rewrite(){
        File filteredTrajFile = new File(trajFileOutString);
        FileOutputStream fos = null;
        BufferedWriter bufferedWriter;
        try {
            fos = new FileOutputStream(filteredTrajFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
            for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
                if(i%50000==0)
                    System.out.println(i+"/"+TrajUtil.trajectoryDataset.size());
                bufferedWriter.write(TrajUtil.getTrajString(i));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
