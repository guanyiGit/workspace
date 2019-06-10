package com.gy.fof.step2;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class FofStep2GroupingComparator extends WritableComparator {

    public FofStep2GroupingComparator() {
        super(FoFEntity.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        FoFEntity fof1 = (FoFEntity) a;
        FoFEntity fof2 = (FoFEntity) b;
        return fof1.getUname().equals(fof2.getUname()) ? 0:1;
    }
}
