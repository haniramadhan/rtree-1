package main.java.CustomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stats {
    private  static final int MIN = 0, MAX = 1;
    public static double computeSkewness(List<List<double[]>> trajSet){
        double[] meanPoint = new double[TrajUtil.getDim()];
        double skewNominator = 0;
        double skewDenominator = 0;
        int nPoint = 0;
        for(List<double[]> traj:trajSet){
            for (double[] p : traj) {
                for (int k = 0; k < TrajUtil.getDim(); k++)
                    meanPoint[k] = meanPoint[k] + p[k];
                nPoint++;
            }

        }
        for(int k=0;k<TrajUtil.getDim();k++)
            meanPoint[k] = meanPoint[k]/nPoint;

        //skewness
        for (List<double[]> traj : trajSet) {
            for (double[] p : traj) {
                double dist = Util.computeDistance(meanPoint, p);
                skewNominator += Math.pow(dist, 3);
                skewDenominator += Math.pow(dist, 2);
            }
        }
        skewNominator/=nPoint;
        skewDenominator/=nPoint;
        skewDenominator= Math.pow(skewDenominator,3.0/2);
        double skewness = skewNominator/skewDenominator;
        System.out.println("Point distribution skewness: " +skewness);

        return skewness;
    }

    public static double[] computeConsecutiveDistPercentiles(List<List<double[]>> trajSet){

        double[] percentilesDistConsecutivePoints = new double[101];
        List<Double> consecutiveDistances = new ArrayList<>();
        for(List<double[]> traj:trajSet) {
            for (int i = 1; i < traj.size(); i++) {
                double consecutiveDist = Util.computeDistance(traj.get(i), traj.get(i-1));
                consecutiveDistances.add(consecutiveDist);
            }
        }
        extractXiles(consecutiveDistances,percentilesDistConsecutivePoints);
        return percentilesDistConsecutivePoints;
    }

    private static  void extractXiles(List<Double> distances, double[] iles) {
        Collections.sort(distances);

        for(int i=0;i<iles.length;i++) {
            if(i==iles.length-1)
                iles[i] = distances.get(distances.size() - 1);
            else
                iles[i] = distances.get((int)((double)distances.size() / iles.length * i ));
        }
    }

    public static double[] computeMbrDiagonal(List<List<double[]>> trajSet){
        double[] quartilesDistMbr = new double[5];
        List<Double> distances = new ArrayList<>();

        for(List<double[]> traj:trajSet) {
            double[][] mbr = TrajUtil.findMBR(traj);
            double[] range = new double[mbr[0].length];

            double dist = Util.computeDistance(mbr[MIN],mbr[MAX]);
            distances.add(dist);
        }
        extractXiles(distances, quartilesDistMbr);
        return quartilesDistMbr;
    }

    public static double[][] computeBoundary(List<List<double[]>> trajSet){

        double[][] boundary = new double[2][];
        boundary[MIN] = new double[TrajUtil.getDim()];
        boundary[MAX] = new double[TrajUtil.getDim()];

        for(int i=0;i<TrajUtil.getDim();i++){
            boundary[MIN][i] = Double.MAX_VALUE;
            boundary[MAX][i] = -Double.MAX_VALUE;
        }

        for(List<double[]> traj:trajSet) {
            for(int d=0;d<TrajUtil.getDim();d++){
                double[][] mbr = TrajUtil.findMBR(traj);
                if (boundary[MIN][d] > mbr[MIN][d])//min
                    boundary[MIN][d] = mbr[MIN][d];
                if (boundary[MAX][d] < mbr[MAX][d])//max
                    boundary[MAX][d] = mbr[MAX][d];
            }
        }
        return boundary;
    }

    public static double computeAverageLength(List<List<double[]>> trajSet){
        double sumLength = 0;
        for(List<double[]> traj:trajSet) {
            sumLength+=traj.size();
        }
        return sumLength/trajSet.size();
    }

}
