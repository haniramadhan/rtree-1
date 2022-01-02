package Xcrtree;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.BitSet;

public class SubTrajTable  implements Serializable {
        //default serialVersion id
        private static final long serialVersionUID = 1L;
        private final TIntArrayList tid;
        private final TIntArrayList lastIndex;
        private final TDoubleArrayList coef,error;
        private final BitSet firstSubs;
        private final TIntArrayList firstSids;


        public SubTrajTable(){
            super();
            tid = new TIntArrayList();
            lastIndex = new TIntArrayList();
            coef = new TDoubleArrayList();
            error = new TDoubleArrayList();
            firstSubs = new BitSet();
            firstSids = new TIntArrayList();
        }

        public BitSet getFirstSubs(){
            return firstSubs;
        }

        public int getFirstSidOfTid(int tid){
            return firstSids.get(tid);
        }


        public void addEntry(int trajTid, int lastIndex){
            tid.add(trajTid);
            this.lastIndex.add(lastIndex);
            if(tid.size()==1){
                firstSubs.set(0);
                firstSids.add(0);
            }
            else{
                if(trajTid == firstSids.get(firstSids.size()-1)){
                    firstSubs.set(firstSids.size()-1);
                    firstSids.add(firstSids.size()-1);
                }
            }
        }

        public int[] getSubTrajAt(int ptr){
            return new int[]{tid.get(ptr),
                    lastIndex.get(ptr)
            };
        }
        public int getTidOfPtr(int ptr){
            if(ptr<0)
                ptr = -ptr;
            return tid.get(ptr);
        }

        public boolean isLastSub(int ptr){
            if(ptr<0)
                ptr = -ptr;
            if (ptr == getLength() - 1)
                return true;
            return tid.get(ptr) != tid.get(ptr+1);
        }

        public boolean isFirstSub(int ptr){
            if(ptr<0)
                ptr = -ptr;

            if(ptr==0)
                return true;
            return tid.get(ptr) != tid.get(ptr-1);
        }

        public int getLastIndex(int ptr){
            return lastIndex.get(ptr);
        }

        public int getSubLength(int ptr){
            if(ptr==0 || tid.get(ptr) != tid.get(ptr-1))
                return lastIndex.get(ptr);
            return lastIndex.get(ptr) - lastIndex.get(ptr-1);
        }

        public int getLength() {
            return tid.size();
        }

        public double getCoefOf(int ptr){
            return coef.get(ptr);
        }

        public double getCountErrorOf(int ptr){
            return error.get(ptr);
        }

        public double getDistanceOf(int ptr){
            return getSubLength(ptr)  /getCoefOf(ptr);
        }

        public BitSet extractTids(BitSet sidResult){
            BitSet tids = new BitSet();
            for(int i=0;i!=-1;i = sidResult.nextSetBit(i+1))
                tids.set(getTidOfPtr(i));
            return tids;
        }

        public double getAverageSubTrajLength(){
            return 1.0*tid.size()/firstSids.size();
        }
}
