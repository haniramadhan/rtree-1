package com.conversantmedia.util.collection.geometry;

import com.conversantmedia.util.collection.spatial.HyperPoint;
import com.conversantmedia.util.collection.spatial.HyperRect;
import com.conversantmedia.util.collection.spatial.RTree;
import com.conversantmedia.util.collection.spatial.RectBuilder;

import java.io.Serializable;

public class PointNd implements HyperPoint, Serializable {

    public double[] point;


    public double[] getPoint(){
        return point;
    }
    public PointNd(final double[] p){
        double[] point_ = new double[p.length];
        for(int i=0;i<p.length;i++){
            point_[i] = p[i];
        }
        point = point_;
    }
    @Override
    public int getNDim() {
        return point.length;
    }

    @Override
    public Double getCoord(int d) {
        return point[d];
    }

    @Override
    public double distance(HyperPoint p) {
       final PointNd p2 = (PointNd) p;

       double dist = 0;
       for(int i=0;i<getNDim();i++){
           double diff = point[i] - p2.getCoord(i);
           dist += diff*diff;
       }
       return Math.sqrt(dist);
    }

    @Override
    public double distance(HyperPoint p, int d) {
        final PointNd p2 = (PointNd) p;
        return Math.abs(point[d]-p2.getCoord(d));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        double dist = 0;

        final PointNd p = (PointNd)o;
        dist = distance(p);

        return dist <= 1e-12;
    }


    @Override
    public int hashCode() {

        int hashcode = Double.hashCode(point[0]);
        int base = 31;
        int base_iter = 31;
        for(int i=1;i<getNDim();i++){
            hashcode = hashcode ^ base_iter* Double.hashCode(point[i]);
            base_iter = base_iter *base;

        }
        return hashcode;
    }

    public final static class Builder implements RectBuilder<PointNd>, Serializable {

        @Override
        public HyperRect getBBox(final PointNd point) {
            return new RectNd(point);
        }

        @Override
        public HyperRect getMbr(final HyperPoint p1, final HyperPoint p2) {
            return new RectNd((PointNd)p1, (PointNd)p2);
        }
    }
}
