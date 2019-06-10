package com.gy.fof.step2;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class FofStep2SortComparator extends WritableComparator {

    public FofStep2SortComparator() {
        super(FoFEntity.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        FoFEntity fof1 = (FoFEntity) a;
        FoFEntity fof2 = (FoFEntity) b;
        boolean f1 = fof1.getUname().equals(fof2.getUname());
        if(f1){
             return -fof1.getScore() < fof2.getScore()?-1:fof1.getScore() == fof2.getScore()?0:1;
        }
        return 1;
    }
}
