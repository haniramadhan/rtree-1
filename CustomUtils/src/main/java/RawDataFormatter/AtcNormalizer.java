package RawDataFormatter;

import main.java.CustomUtils.TrajUtil;

import java.io.*;
import java.util.List;

public class AtcNormalizer {


    static String trajFileInString = "../dataset/atc-x-v2.csv";
    static String trajFileOutString = "../dataset/atc-v2-normal.csv";
    public static void main(String[] args){
        File formattedFile = new File(trajFileInString);
        TrajUtil.loadTrajDatasetFromFile(formattedFile.getPath());

        int MIN = 0, MAX=1;
        double[][] mbr = new double[2][];
        mbr[MIN] = new double[TrajUtil.getDim()];
        mbr[MAX] = new double[TrajUtil.getDim()];

        for(int i=0;i<TrajUtil.getDim();i++){
            mbr[MIN][i] = Double.MAX_VALUE;
            mbr[MAX][i] = -Double.MAX_VALUE;
        }
        for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
            double[][] trajMbr = TrajUtil.findMBR(TrajUtil.trajectoryDataset.get(i));
            for(int j=0;j<TrajUtil.getDim();j++){
                if(mbr[MIN][j]>trajMbr[MIN][j])
                    mbr[MIN][j] = trajMbr[MIN][j];
                if(mbr[MAX][j]<trajMbr[MAX][j])
                    mbr[MAX][j] = trajMbr[MAX][j];
            }
        }

        for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            for(int p=0;p<traj.size();p++) {
                double[] pointNew = new double[TrajUtil.getDim()];
                for (int j = 0; j < TrajUtil.getDim(); j++) {
                     pointNew[j] = (traj.get(p)[j] - mbr[MIN][j])/(mbr[MAX][j] - mbr[MIN][j]);
                }
                traj.set(p,pointNew);
            }
            TrajUtil.trajectoryDataset.set(i,traj);
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
