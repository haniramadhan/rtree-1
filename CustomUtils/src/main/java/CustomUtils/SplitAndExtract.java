package main.java.CustomUtils;

//import SubTrajDensity.TrajDensityLr;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

public class SplitAndExtract {
    private static final int MIN = 0;
    private static final int MAX = 1;
    private static final int FIRST = 2;
    private static final int LAST = 3;
    private static final int SUBMIN = 4;
    private static final int SUBMAX = 5;
    private static final int SINGLES = 6;

    private static List<double[]> mbrMins;
    private static List<double[]> mbrMaxs;
    private static List<double[]> firsts;
    private static List<double[]> lasts;
    private static List<double[]> subMbrMins;
    private static List<double[]> subMbrMaxs;
    private static List<double[]> subSingles;

    private static List<List<double[]>> subPoints;


    private static List<double[]> subTrajBall;
    private static TDoubleArrayList radiusSubs;


    private static TIntArrayList ptrNonSubs;
    private static TIntArrayList ptrSubs;

    public static void initCollections(){
        mbrMins = new ArrayList<>();
        mbrMaxs = new ArrayList<>();
        firsts = new ArrayList<>();
        lasts = new ArrayList<>();
        subMbrMins = new ArrayList<>();
        subMbrMaxs = new ArrayList<>();
        subSingles = new ArrayList<>();

        ptrNonSubs = new TIntArrayList();
        ptrSubs = new TIntArrayList();
    }

    public static void initTrajCollections(){
        mbrMins = new ArrayList<>();
        mbrMaxs = new ArrayList<>();
        firsts = new ArrayList<>();
        lasts = new ArrayList<>();
        ptrNonSubs = new TIntArrayList();

    }

    public static void initSubTrajCollections(){
        subMbrMins = new ArrayList<>();
        subMbrMaxs = new ArrayList<>();
        subSingles = new ArrayList<>();

        ptrSubs = new TIntArrayList();

    }

    public static List<TIntArrayList> splitTrajDbInto(int splitNumber){
        List<TIntArrayList> splitIndices = new ArrayList<>();
        int trSize = TrajUtil.trajectoryDataset.size();
        for(int i=0;i<trSize;i++) {
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            TIntArrayList split = MoreTrajUtil.heurSplit(traj, splitNumber - 1);
            splitIndices.add(split);
        }
        return splitIndices;
    }


    public static List<TIntArrayList> splitTrajDbRange(double[] rangeThreshold){
        List<TIntArrayList> splitIndices = new ArrayList<>();
        int trSize = TrajUtil.trajectoryDataset.size();

        for(int i=0;i<trSize;i++) {
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            TIntArrayList split = MoreTrajUtil.heurSplitWithRange(traj,rangeThreshold);
            splitIndices.add(split);
        }
        return splitIndices;
    }


    public static void extractSubPointFromDatasetV3(List<TIntArrayList> splitIndices){
        int trSize = TrajUtil.trajectoryDataset.size();
        int iSubs = 0;
        for(int i=0;i<trSize;i++){
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            List<double[]>[] subTrajs = splitTrajFromIndices(traj,splitIndices.get(i));
            for (List<double[]> subTraj : subTrajs) {
                double[][] subMbr = TrajUtil.findMBR(subTraj);
                subMbrMins.add(subMbr[MIN]);
                subMbrMaxs.add(subMbr[MAX]);
                ptrSubs.add(iSubs++);
            }
        }
    }


    public static List<List<double[]>> getPointCollectionsV3(){
        List<List<double[]>> pointCollection = new ArrayList<>();
        pointCollection.add(mbrMins);
        pointCollection.add(mbrMaxs);
        pointCollection.add(firsts);
        pointCollection.add(lasts);
        pointCollection.add(subMbrMins);
        pointCollection.add(subMbrMaxs);

        return pointCollection;
    }
    public static List<List<double[]>> getPointCollectionsV5(){
        List<List<double[]>> pointCollection = new ArrayList<>();
        pointCollection.add(mbrMins);
        pointCollection.add(mbrMaxs);
        pointCollection.add(subMbrMins);
        pointCollection.add(subMbrMaxs);

        return pointCollection;
    }

    public static List<TIntArrayList> getPtrCollectionsV3(){
        List<TIntArrayList> ptrCollection = new ArrayList<>();
        ptrCollection.add(ptrNonSubs);
        ptrCollection.add(ptrSubs);
        return ptrCollection;
    }

    public static void extractTrajLevelPoints(){
        int trSize = TrajUtil.trajectoryDataset.size();
        for(int i=0;i<trSize;i++){
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            double[][] mbr = TrajUtil.findMBR(traj);
            mbrMins.add(mbr[MIN]);
            mbrMaxs.add(mbr[MAX]);
            firsts.add(traj.get(0));
            lasts.add(traj.get(traj.size()-1));
            ptrNonSubs.add(i);

        }
    }

    public static void extractPointAndSub(List<TIntArrayList> splitIndices){
        subMbrMaxs = new ArrayList<>();
        subMbrMins = new ArrayList<>();
        ptrSubs = new TIntArrayList();
        int trSize = TrajUtil.trajectoryDataset.size();
        int iSubs = 0;
        for(int i=0;i<trSize;i++){
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            List<double[]>[] subTrajs = SplitAndExtract.splitTrajFromIndices(traj,splitIndices.get(i));
            for (List<double[]> subTraj : subTrajs) {
                double[][] subMbr = TrajUtil.findMBR(subTraj);
                subMbrMins.add(subMbr[MIN]);
                subMbrMaxs.add(subMbr[MAX]);
                ptrSubs.add(iSubs++);
            }
        }
    }
    public static List<List<double[]>> getSubTrajPoints(){
        List<List<double[]>> pointCollection = new ArrayList<>();
        pointCollection.add(subMbrMins);
        pointCollection.add(subMbrMaxs);

        return pointCollection;
    }

    public static TIntArrayList getPtrCollectionsV5(){
        return  ptrSubs;
    }
    public static List<double[]>[] splitSingleTraj(List<double[]>traj, int split){
        return splitTrajFromIndices(traj, MoreTrajUtil.heurSplit(traj,split-1));
    }

    public static List<double[]>[] splitSingleTrajByRange(List<double[]>traj,double[] range){
        return splitTrajFromIndices(traj, MoreTrajUtil.heurSplitWithRange(traj,range));
    }
    public static List<double[]>[] splitTrajFromIndices(List<double[]> traj, TIntArrayList splitIndices){
        List[] subTrajs = new List[splitIndices.size()];
        for(int j=0;j<splitIndices.size();j++) {
            List<double[]> subTraj;
            if (j == 0)
                subTraj = traj.subList(0, splitIndices.get(j));
            else
                subTraj = traj.subList(splitIndices.get(j-1), splitIndices.get(j));
            subTrajs[j] = subTraj;
        }
        return subTrajs;
    }

    public static List<TIntArrayList> splitTrajDbIntoV5(double criteria){
        List<TIntArrayList> splitIndices = new ArrayList<>();
        int trSize = TrajUtil.trajectoryDataset.size();
        for(int i=0;i<trSize;i++) {
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            TIntArrayList split = MoreTrajUtil.splitByCriteria(traj,criteria);
            splitIndices.add(split);
        }
        return splitIndices;
    }


    public static List<List<double[]>> getTrajPointCollection(){
        List<List<double[]>> pointCollection = new ArrayList<>();
        pointCollection.add(mbrMins);
        pointCollection.add(mbrMaxs);
        return pointCollection;
    }
    public static TIntArrayList getPtrNonSubs(){
        return ptrNonSubs;
    }
    public static TIntArrayList getPtrSubs(){
        return ptrSubs;
    }
}
