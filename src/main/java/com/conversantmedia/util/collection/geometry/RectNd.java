package com.conversantmedia.util.collection.geometry;

import com.conversantmedia.util.collection.spatial.HyperPoint;
import com.conversantmedia.util.collection.spatial.HyperRect;
import com.conversantmedia.util.collection.spatial.RectBuilder;

import java.io.Serializable;

public class RectNd implements HyperRect, Serializable {
    private final PointNd min, max;
    final int id;

    RectNd(final PointNd p){
        min = new PointNd(p.getPoint());
        max = new PointNd(p.getPoint());
        id = -1;
    }
    RectNd(final PointNd p, int id_){
        min = new PointNd(p.getPoint());
        max = new PointNd(p.getPoint());
        id = id_;
    }

    public RectNd(final double[] p1, final double[] p2, int id_){
        min = new PointNd(p1);
        max = new PointNd(p2);
        id = id_;
    }
    public RectNd(final PointNd p1, final PointNd p2, int id_){

        final double[] minP = new double[p1.getNDim()];
        final double[] maxP = new double[p1.getNDim()];
        for(int i=0;i<p1.getNDim();i++){
            minP[i] = p1.getCoord(i);
            maxP[i] = p2.getCoord(i);
            if(minP[i]>maxP[i]){
                maxP[i] = p1.getCoord(i);;
                minP[i] = p2.getCoord(i);;
            }
        }

        min = new PointNd(minP);
        max = new PointNd(maxP);
        id = id_;
    }
    public RectNd(final double[] p1, final double[] p2){
        min = new PointNd(p1);
        max = new PointNd(p2);
        id = -1;
    }
    public RectNd(final PointNd p1, final PointNd p2){

        final double[] minP = new double[p1.getNDim()];
        final double[] maxP = new double[p1.getNDim()];
        for(int i=0;i<p1.getNDim();i++){
            minP[i] = p1.getCoord(i);
            maxP[i] = p2.getCoord(i);
            if(minP[i]>maxP[i]){
                maxP[i] = p1.getCoord(i);;
                minP[i] = p2.getCoord(i);;
            }
        }

        min = new PointNd(minP);
        max = new PointNd(maxP);
        id = -1;
    }

    @Override
    public HyperRect getMbr(HyperRect r) {
        final RectNd r2 = (RectNd) r;
        final double[] minP = new double[r2.getNDim()];
        final double[] maxP = new double[r2.getNDim()];
        for(int i=0;i<r2.getNDim();i++){
            minP[i] = Math.min(r2.min.getCoord(i),min.getCoord(i));
            maxP[i] = Math.max(r2.max.getCoord(i),max.getCoord(i));

        }
        return new RectNd(minP,maxP);
    }

    @Override
    public int getNDim() {
        return min.getNDim();
    }

    @Override
    public HyperPoint getMin() {
        return min;
    }

    @Override
    public HyperPoint getMax() {
        return max;
    }

    @Override
    public HyperPoint getCentroid() {
        final double[] center = new double[getNDim()];
        for(int i=0;i<getNDim();i++){
            center[i] = (min.getCoord(i) + max.getCoord(i)) / 2.0;
        }

        return new PointNd(center);
    }

    @Override
    public double getRange(int d) {

        if(d <= getNDim()) {
            return max.getCoord(d) - min.getCoord(d);
        } else {
            throw new IllegalArgumentException("Invalid dimension");
        }
    }

    @Override
    public boolean contains(HyperRect r) {
        final RectNd r2 = (RectNd) r;
        for(int i=0;i<getNDim();i++){
            if(!(min.getCoord(i)<=r2.min.getCoord(i) &&
                    r2.max.getCoord(i) <= max.getCoord(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean intersects(HyperRect r) {
        final RectNd r2 = (RectNd) r;
        for(int i=0;i<getNDim();i++){
            if(!(!(min.getCoord(i) > r2.max.getCoord(i)) &&
                   !(r2.min.getCoord(i) > max.getCoord(i)))){
                return false;
            }
        }
        return true;
    }

    @Override
    public double cost() {

        double d = 1;
        for(int i=0;i<getNDim();i++){
            d *= (max.getCoord(i) - min.getCoord(i));
        }
        return Math.abs(d);
    }

    @Override
    public double perimeter() {
        double p = 0.0;
        final int nD = this.getNDim();
        for(int d = 0; d<nD; d++) {
            p += 4.0 * this.getRange(d);
        }
        return p;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RectNd rectNd = (RectNd) o;

        return min.equals(rectNd.min) &&
                max.equals(rectNd.max);
    }

    @Override
    public int hashCode() {
        return min.hashCode() ^ 31*max.hashCode();
    }

    public String toString() {

        String minS = "(";
        String maxS = "(";

        for(int i=0;i<getNDim();i++){
            minS = minS + min.getCoord(i) +",";
            maxS = maxS + max.getCoord(i) +",";
        }
        minS += ")";
        maxS += ")";


        return minS +" "+maxS;
    }

    public final static class Builder implements RectBuilder<RectNd>, Serializable {

        @Override
        public HyperRect getBBox(final RectNd rectND) {
            return rectND;
        }

        @Override
        public HyperRect getMbr(final HyperPoint p1, final HyperPoint p2) {

            return new RectNd((PointNd)p1,(PointNd)p2);
        }
    }
}
