package Xrtree;

import com.conversantmedia.util.collection.geometry.Rect2d;
import com.conversantmedia.util.collection.geometry.RectNd;
import com.conversantmedia.util.collection.spatial.RTree;
import com.conversantmedia.util.collection.spatial.RTreeTest;
import com.conversantmedia.util.collection.spatial.Stats;
import main.java.CustomUtils.TrajUtil;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class DatasetTest {

    public static final String dataPath = "../dataset/";
    public static final String sampleDatasetChengduNew = dataPath+"chengdu-tiny.csv";
    public static final String sampleDatasetAtc = dataPath+"atc-v2-sample.csv";
    public static final String datasetAtcFull = dataPath+"atc-x-normal.csv";
    public static final String datasetChengduFull = dataPath+"chengdu-new-00.csv";
    public static final String datasetPortoFull = dataPath+"porto.csv";
    public static final String datasetPortoSample = dataPath+"porto-sample.csv";
    public static String sampleDataset;

    public static final String sampleTestChengduNew  = dataPath+"chengdu_sample_test.csv";


    public static final String sampleTestAtc = dataPath+"atc-v2-sample-test.csv";
    public static final String sampleTestPorto = dataPath+"porto-sample-test.csv";
    @Test
    public void loadDataset(){

        RTree<RectNd> rTree = new RTree<>(new RectNd.Builder(), 2, 50, RTree.Split.QUADRATIC);


        TrajUtil.loadTrajDatasetFromFile(sampleDatasetChengduNew);
        for(int i=0;i<TrajUtil.trajectoryDataset.size();i++){
            double[][] mbr = TrajUtil.findMBR(TrajUtil.trajectoryDataset.get(i));
            rTree.add(new RectNd(mbr[0],mbr[1]));
        }
        Stats stats = rTree.collectStats();
        WriteObjectToFile(rTree,"rtree-conv.iobj");
    }


    public void WriteObjectToFile(Object serObj, String filepath) {

        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
