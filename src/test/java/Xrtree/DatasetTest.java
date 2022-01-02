package Xrtree;

import Xcrtree.SubTrajTable;
import com.conversantmedia.util.collection.geometry.Rect2d;
import com.conversantmedia.util.collection.geometry.RectNd;
import com.conversantmedia.util.collection.spatial.RTree;
import com.conversantmedia.util.collection.spatial.RTreeTest;
import com.conversantmedia.util.collection.spatial.Stats;
import gnu.trove.list.array.TIntArrayList;
import main.java.CustomUtils.SplitAndExtract;
import main.java.CustomUtils.TrajUtil;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

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
            rTree.add(new RectNd(mbr[0],mbr[1],i));
        }

        Stats stats = rTree.collectStats();
        WriteObjectToFile(rTree,"rtree-conv.iobj");
        List<TIntArrayList> splitIndex = buildSplitIndex();
        RTree<RectNd> rTreeSub = buildSubtrajTree(splitIndex);
        SubTrajTable table = buildTable(splitIndex);
        WriteObjectToFile(rTreeSub,"rtree-sub-conv.iobj");
        WriteObjectToFile(table,"subtrajtable.iobj");

    }

    public RTree<RectNd> buildSubtrajTree(List<TIntArrayList> splitIndicesv5){
        SubTrajTable table = buildTable(splitIndicesv5);

        RTree<RectNd> rTree = new RTree<>(new RectNd.Builder(), 2, 50, RTree.Split.QUADRATIC);
        int id= 0;
        for(int i=0; i < splitIndicesv5.size(); i++) {
            List<double[]> traj = TrajUtil.trajectoryDataset.get(i);
            System.out.println(i+"/"+ splitIndicesv5.size() );

            for (int j = 0; j < splitIndicesv5.get(i).size(); j++) {
                double[][] mbr = null;
                int lastIndexSub = splitIndicesv5.get(i).get(j);
                if(j==0)
                    mbr = TrajUtil.findMBR(traj.subList(0,lastIndexSub));
                else{
                    mbr = TrajUtil.findMBR(traj.subList(splitIndicesv5.get(i).get(j-1),lastIndexSub));
                }
                rTree.add(new RectNd(mbr[0],mbr[1],id++));
            }
        }

        return rTree;
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
    public List<TIntArrayList> buildSplitIndex(){
        SplitAndExtract.initCollections();
        List<TIntArrayList> splitIndicesv5 = SplitAndExtract.splitTrajDbIntoV5(0.07);

        return splitIndicesv5;

    }
    public static SubTrajTable buildTable(List<TIntArrayList> splitIndices) {

        SubTrajTable table = new SubTrajTable();

        for (int i = 0; i < splitIndices.size(); i++) {
            for (int j = 0; j < splitIndices.get(i).size(); j++) {
                table.addEntry(i, splitIndices.get(i).get(j));
            }
        }
        return table;
    }
}
