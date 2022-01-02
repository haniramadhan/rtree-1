package main.java.CustomUtils;

import java.util.List;

public class Distance {
    public static final int DTW = 1;
    public static final int FRECHET = 0;
    public static final int LCSS = 2;
    public static final int EDR = 3;
    public static final int ERP = 3;

    public static int currentDistance = FRECHET;
    public static double eps = 0.003;
    public static int spaceConstraint = 1;
    public static double[][] distance;
    public static double[] erpRef;


    public static void setEps(double eps_){
        eps = eps_;
    }

    public static boolean isCurDistDfDtw(){
        return currentDistance == FRECHET || currentDistance == DTW;
    }

    public static void setParameters(double eps_, int space){
        setEps(eps_);
        setSpaceConstraint(space);
    }
    public static void setSpaceConstraint(int space_){
        spaceConstraint = space_;
    }
    public static void setErpRef(double[] erpRef_){
        erpRef = erpRef_;
    }

    public static void setCurrentDistance(int distanceType){
        currentDistance = distanceType;
    }

    public static String getDistanceStr(){
        switch (currentDistance){
            case DTW:
                return "dtw";
            case LCSS:
                return "lcss";
            case EDR:
                return "edr";
            default:
                return "frechet";
            //case FRECHET:
            //return "frechet";
        }
    }

    public static void initMinDistance(int t1Size, int t2Size){
        distance = new double[t1Size+1][];
        for(int i=0;i<=t1Size;i++){
            distance[i] = new double[t2Size+1];
        }

    }

    public static double computeDistance(List<double[]> trajQ, List<double[]> trajR){
        switch(currentDistance){
            case DTW:
                return computeDTW(trajQ,trajR);

            case FRECHET:
                return computeFrechet(trajQ,trajR);

            case EDR:
                return computeEDR(trajQ,trajR, eps);

            case LCSS:
                return computeLCSS(trajQ,trajR, eps, spaceConstraint);
        }
        return -1;
    }

    public static double computeDTW(List<double[]> t1, List<double[]> t2){
        initMinDistance(t1.size(),t2.size());

        for(int i=1;i<=t1.size();i++){
            for(int j=1;j<=t2.size();j++){
                computeDTW(i,j,t1,t2);
            }
        }

        return distance[t1.size()][t2.size()];
    }

    public static double computeEDR(List<double[]> t1, List<double[]> t2, double threshold){
        initMinDistance(t1.size(),t2.size());
        for(int i=0;i<=t1.size();i++)
            distance[i][0]=i;
        for(int i=0;i<=t2.size();i++)
            distance[0][i]=i;
        for(int i=0;i<t1.size();i++){
            for(int j=0;j<t2.size();j++){
                double dist = Util.computeDistance(t1.get(i),t2.get(j));
                int notMatch = 0;
                if(dist>threshold)
                    notMatch = 1;
                distance[i+1][j+1] = distance[i][j] + notMatch;
                distance[i+1][j+1] = Math.min(distance[i+1][j] + 1, distance[i+1][j+1]);
                distance[i+1][j+1] = Math.min(distance[i][j+1] + 1, distance[i+1][j+1]);
            }
        }
        return distance[t1.size()][t2.size()];
    }

    public static double computeLCSS(List<double[]> t1, List<double[]> t2, double threshold, int maxIndexDiff){
        initMinDistance(t1.size(),t2.size());

        distance[0][0] = 0;
        for(int i=1;i<=t1.size();i++)
            distance[i][0] = i;

        for(int j=1;j<=t2.size();j++)
            distance[0][j] = j;

        for(int i=1;i<=t1.size();i++){
            for(int j=1;j<=t2.size();j++){
                double dist = Util.computeDistance(t1.get(i-1),t2.get(j-1));
                if(dist>threshold || Math.abs(i-j)>maxIndexDiff)
                    distance[i][j] = Math.min(distance[i-1][j], distance[i][j-1])+1;
                else
                    distance[i][j] = distance[i-1][j-1];
            }
        }

        return distance[t1.size()][t2.size()];
    }

    public static double computeFrechet(List<double[]> t1, List<double[]> t2){
        initMinDistance(t1.size(),t2.size());

        distance[0][0] = Util.computeDistance(t1.get(0),t2.get(0));

        for(int i=0;i<t1.size();i++){
            for(int j=0;j<t2.size();j++){
                if(i==0&&j==0) continue;
                double dist = Util.computeDistance(t1.get(i),t2.get(j));
                if(i==0) {
                    distance[i][j] = Math.max(dist, distance[i][j-1]);
                    continue;
                }
                if(j==0){
                    distance[i][j] = Math.max(dist, distance[i-1][j]);
                    continue;
                }
                double distMin = Math.min(distance[i][j-1], distance[i-1][j]);
                distMin = Math.min(distMin, distance[i-1][j-1]);
                dist = Math.max(dist,distMin);
                distance[i][j] = dist;
            }
        }

        return distance[t1.size()-1][t2.size()-1];
    }


    public static void computeDTW(int i1, int i2, List<double[]> t1, List<double[]> t2){
        double dist = 0;
        if(i1==1){
            for(int i=0;i<i2;i++){
                dist = dist + Util.computeDistance(t1.get(0),t2.get(i));
            }
        }
        else if(i2 == 1){
            for(int i=0;i<i1;i++){
                dist = dist + Util.computeDistance(t1.get(i),t2.get(0));
            }
        }
        else{
            dist = dist + Util.computeDistance(t1.get(i1-1),t2.get(i2-1));
            dist = dist + Math.min(Math.min(distance[i1-1][i2-1], distance[i1][i2-1]),
                    distance[i1-1][i2]);
        }
        distance[i1][i2] = dist;
    }


    public static void computeERP(List<double[]> t1, List<double[]> t2){
        if(erpRef==null){
            erpRef = new double[TrajUtil.getDim()];
        }
        computeERP(t1,t2,erpRef);
    }
    public static void computeERP(List<double[]> t1, List<double[]> t2, double[] ref){

        for(int i=1;i<=t1.size();i++)
            distance[i][0]=Util.computeDistance(t1.get(i-1),ref);
        for(int i=1;i<=t2.size();i++)
            distance[0][i]=Util.computeDistance(t2.get(i-1),ref);
        for(int i=0;i<t1.size();i++){
            for(int j=0;j<t2.size();j++){
                double distRefT1 = Util.computeDistance(t1.get(i),ref);
                double distRefT2 = Util.computeDistance(ref,t2.get(j));
                double distT1T2 = Util.computeDistance(t1.get(i),t2.get(j));

                distance[i+1][j+1] = distance[i][j] + distT1T2;
                distance[i+1][j+1] = Math.min(distance[i+1][j] + distRefT2, distance[i+1][j+1]);
                distance[i+1][j+1] = Math.min(distance[i][j+1] + distRefT1, distance[i+1][j+1]);
            }
        }
    }
}
