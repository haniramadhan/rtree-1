package main.java.CustomUtils;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class ResultOpUtil {



    public static final int INDEX_MIN = 0;
    public static final int INDEX_MAX = 1;

    public static final int INDEX_FIRST = 2;
    public static final int INDEX_LAST = 3;

    public static final int SUBSTART = 2;
    public static final int INDEX_SUBMIN = 4;
    public static final int INDEX_SUBMAX = 5;

    public static TIntList addAll(TIntList a, TIntList b){
        TIntList c = new TIntArrayList();
        int i=0, j=0;
        while(i<a.size() && j < b.size()){
            if(a.get(i) < b.get(j))
                c.add(a.get(i++));
            else if(a.get(i) > b.get(j))
                c.add(b.get(j++));
            else if(a.get(i)==b.get(j)){
                c.add(a.get(i++));
                j++;
            }
        }
        while(i<a.size())
            c.add(a.get(i++));

        while(j<b.size())
            c.add(b.get(j++));
        return c;
    }


    public static TIntList retainAll(TIntList a, TIntList b){
        TIntList c = new TIntArrayList();
        int i=0, j=0;

        while(i<a.size() && j < b.size()){
            if(a.get(i) < b.get(j))
                i++;
            else if(a.get(i) > b.get(j))
                j++ ;
            else if(a.get(i)==b.get(j)){
                c.add(a.get(i++));
                j++;
            }

        }
        return c;
    }


    public static List<Util.IntDblPair> retainAllEdr(List<Util.IntDblPair> a, TIntList b) {

        int i=0, j=0;
        while(i<a.size() && j < b.size()) {
            if (a.get(i).intVal < b.get(j))
                i++;
            else if (a.get(i).intVal > b.get(j))
                j++;
            else if (a.get(i).intVal == b.get(j)) {
                a.get(i).dblVal = a.get(i).dblVal - 1;
                i++;
                j++;
            }
        }
        return a;
    }
    public static List<Util.IntDblPair> retainAll(BitSet a, List<Util.IntDblPair> b) {

        List<Util.IntDblPair> c = new ArrayList<>();
        int i=0;
        while(i < b.size()){
            if(a.get(b.get(i).intVal)){
                c.add(b.get(i));
            }
            i++;
        }
        return c;
    }
    public static float[][] populateMbrPoints(TIntList result, int dim, boolean isMin){
        float[][] mbrPoints = new float[result.size()][];
        for(int i=0;i<result.size();i++) {
            int tid = result.get(i);
            double[][] mbr = TrajUtil.findMBR(TrajUtil.trajectoryDataset.get(tid));
            float[] mbrPoint = new float[dim];
            for (int d = 0; d < mbr[0].length; d++)
                if (isMin)
                    mbrPoint[d] = (float)mbr[d][0];
                else
                    mbrPoint[d] = (float)mbr[d ][1];
            mbrPoints[i] = mbrPoint;
        }
        return mbrPoints;
    }

    public static float[][] populateFirstLast(TIntList result, int dim, boolean isLast){
        float[][] points = new float[result.size()][];
        for(int i=0;i<result.size();i++) {
            int tid = result.get(i);
            int ii=0;
            if(isLast)
                ii = TrajUtil.trajectoryDataset.get(tid).size()-1;
            points[i] = new float[dim];
            for(int d=0;d<dim;d++)
                points[i][d] = (float) TrajUtil.trajectoryDataset.get(tid).get(ii)[d];
        }
        return points;
    }


    public static TIntList refineGeneric(TIntList result, double[] point, double eps, int pointType){
        TIntList resultRefined = new TIntArrayList();
        int dim = point.length;

        float[][] queriedPoints;
        if(pointType == INDEX_MIN || pointType == INDEX_MAX)
            queriedPoints = populateMbrPoints(result,dim,pointType == INDEX_MIN);
        else
            queriedPoints = populateFirstLast(result, dim, pointType == INDEX_LAST);
        for(int i=0;i<result.size();i++){
            float[] qPoint = queriedPoints[i];
            boolean inRange = true;
            for(int d=0;d<dim;d++){
                if (!(point[d] - eps <= qPoint[d] && qPoint[d] <= qPoint[d] + eps)) {
                    inRange = false;
                    break;
                }
            }
            if(inRange) {
                int tid = result.get(i);
                resultRefined.add(tid);
            }
        }
        return resultRefined;
    }



    public static List<Util.IntDblPair> refineByFirstLast(List<Util.IntDblPair> result, double[] point,
                                                          double eps, boolean isLast){
        List<Util.IntDblPair> resultRefined = new ArrayList<>();
        for(int i=0;i<result.size();i++){
            int tid = result.get(i).intVal;

            int iiTraj = 0;
            if(isLast){
                iiTraj = TrajUtil.trajectoryDataset.get(tid).size()-1;
            }
            double[] pointInDataset = TrajUtil.trajectoryDataset.get(tid).get(iiTraj);

            int dim = point.length;
            boolean inRange = true;
            double dist = 0;
            for(int d=0;d<dim && inRange;d++){
                dist = Util.computeDistance(point,pointInDataset);
                inRange = dist <= eps;
            }
            if(inRange)
                resultRefined.add( new Util.IntDblPair(tid,dist));
        }
        return resultRefined;
    }


    public static List<Util.IntDblPair> addAllDistSortedUnsorted(List<Util.IntDblPair> sorted, List<Util.IntDblPair> unsorted) {
        //

        unsorted.sort(Comparator.comparing(p->p.intVal));

        List<Util.IntDblPair> c = new ArrayList<>();
        int i=0, j=0;
        while(i<sorted.size() && j < unsorted.size()){
            if(sorted.get(i).intVal < unsorted.get(j).intVal)
                c.add(sorted.get(i++));
            else if(sorted.get(i).intVal > unsorted.get(j).intVal)
                c.add(unsorted.get(j++));
            else if(sorted.get(i)==unsorted.get(j)){
                c.add(sorted.get(i++));
                j++;
            }
        }
        while(i<sorted.size())
            c.add(sorted.get(i++));

        while(j<unsorted.size())
            c.add(unsorted.get(j++));
        return c;
    }

    public static List<Util.IntDblPair> addAllDistSortedSorted(List<Util.IntDblPair> a,
                                                                  List<Util.IntDblPair> b) {
        //a.sort(Comparator.comparing(p->p.intVal));
        //b.sort(Comparator.comparing(p->p.intVal));

        List<Util.IntDblPair> c = new ArrayList<>();
        int i=0, j=0;
        while(i<a.size() && j < b.size()){
            if(a.get(i).intVal < b.get(j).intVal) {
                c.add(a.get(i));
                i++;
            }
            else if(a.get(i).intVal > b.get(j).intVal) {
                c.add(b.get(j));
                j++;
            }
            else if(a.get(i).intVal ==b.get(j).intVal){
                double dist = Math.min(a.get(i).dblVal, b.get(j).dblVal);
                c.add(new Util.IntDblPair(b.get(j).intVal, dist));
                i++;
                j++;
            }
        }

        while(i<a.size())
            c.add(a.get(i++));

        while(j<b.size())
            c.add(b.get(j++));
        return c;
    }

    public static List<Util.IntDblPair> retainAllDistSortedSorted(List<Util.IntDblPair> a,
                                                                  List<Util.IntDblPair> b,
                                                                  double eps, boolean combineMin) {
        //a.sort(Comparator.comparing(p->p.intVal));
        //b.sort(Comparator.comparing(p->p.intVal));

        List<Util.IntDblPair> c = new ArrayList<>();
        int i=0, j=0;
        while(i<a.size() && j < b.size()){
            if(a.get(i).intVal < b.get(j).intVal)
                i++;
            else if(a.get(i).intVal > b.get(j).intVal)
                j++ ;
            else if(a.get(i).intVal ==b.get(j).intVal){

                double dist;
                if(combineMin) {
                    dist = Math.min(a.get(i).dblVal, b.get(j).dblVal);
                    c.add(new Util.IntDblPair(b.get(j).intVal, dist));
                }
                else {
                    dist = a.get(i).dblVal + b.get(j).dblVal;
                    if (dist <= eps) {
                        c.add(new Util.IntDblPair(b.get(j).intVal, dist));
                    }
                }
                i++;
                j++;
            }
        }
        return c;
    }

    public static List<Util.IntDblPair> retainAll(TIntList a,
                                                  List<Util.IntDblPair> b){

        List<Util.IntDblPair> c = new ArrayList<>();
        int i=0, j=0;
        while(i<a.size() && j < b.size()){
            if(a.get(i) == b.get(j).intVal){
                c.add(new Util.IntDblPair(b.get(j).intVal,b.get(j).dblVal));
                i++; j++;
            }
            else if(a.get(i) > b.get(j).intVal)
                j++;
            else
                i++;
        }
        return c;
    }
    public static void doSomething(){

    }


    public static BitSet addAll(BitSet a, TIntList b) {
        for(int i=0;i<b.size();i++){
            a.set(b.get(i));
        }
        return a;
    }

    public static BitSet retainAll(BitSet a, TIntList b) {
        BitSet c = new BitSet(a.size());
        for(int i=0;i<b.size();i++){
            if(a.get(b.get(i)))
                c.set(b.get(i));
        }
        return c;
    }
}
