package main.java.CustomUtils;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.Serializable;
import java.util.List;

public class ProtoIndex3 implements Serializable {
    public TIntList ids;
    public TIntList parts;
    public List<TDoubleArrayList> boundBox;


    public ProtoIndex3(TIntList ids, TIntList parts, List<TDoubleArrayList>boundBox){
        this.ids= ids;
        this.parts = parts;
        this.boundBox = boundBox;

    }

}
