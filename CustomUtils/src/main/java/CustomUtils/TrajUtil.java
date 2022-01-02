package main.java.CustomUtils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrajUtil {
    public static List<List<double[]>> trajectoryDataset;
    public static List<List<double[]>> trajectoryQueryset;
    public static List<List<double[]>> samplePointsSet;
    private static int dim;
    private static final int MIN = 0, MAX = 1;

    private static double[][] boundary;


    public static int getDim(){
        return dim;
    }



    public static List<List<double[]>> loadTrajSetFromFile(String path){
        List<List<double[]>> set = new ArrayList<>();
        int i=0;
        int p = 0;
        String ss = "";
        try  {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                i++;
                String[] values = line.split(";");
                List<double[]> traj = new ArrayList<>();
                p = 0;
                ss = line;
                for(String s:values){
                    p++;
                    traj.add(Util.parseCommaDelimited(s));
                }
                //if(i%10000 == 0)
                 //   System.out.println(i);
                set.add(traj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            System.out.println(i+":"+p);
            System.out.println(ss);
        }
        return set;
    }

    public static void loadTrajDatasetFromFile(String path){
        trajectoryDataset = loadTrajSetFromFile(path);
        dim = trajectoryDataset.get(0).get(0).length;
        boundary = Stats.computeBoundary(trajectoryDataset);
    }

    public static double[][] getBoundary(){
        return boundary;
    }

    public static void loadTrajQuerysetFromFile(String path){
        trajectoryQueryset = loadTrajSetFromFile(path);
    }

    public static String getTrajString(int i){
        String format = "%.6f";
        String trajString = String.format(format,trajectoryDataset.get(i).get(0)[0])+"";
        for(int k=1;k<dim;k++)
            trajString += ("," +  String.format(format,trajectoryDataset.get(i).get(0)[k]));
        for(int j=1;j<trajectoryDataset.get(i).size();j++){
            trajString += ";"+ String.format(format,trajectoryDataset.get(i).get(j)[0])+"";
            for(int k=1;k<dim;k++)
                trajString += ("," +  String.format(format,trajectoryDataset.get(i).get(j)[k]));
        }
        return trajString;
    }



    public static double[][] findMBR(List<double[]> traj){
        int dim = traj.get(0).length;
        double[][] mbr = new double[2][dim];

        for(int i=0;i<dim;i++){
            mbr[MIN][i] = Double.MAX_VALUE;
            mbr[MAX][i] = -Double.MAX_VALUE;
        }

        for(int i=0;i<traj.size();i++){
            for(int d=0;d<dim;d++) {
                if (mbr[MIN][d] > traj.get(i)[d])//min
                    mbr[MIN][d] = traj.get(i)[d];
                if (mbr[MAX][d]< traj.get(i)[d])//max
                    mbr[MAX][d] = traj.get(i)[d];
            }
        }
        return mbr;
    }



}
