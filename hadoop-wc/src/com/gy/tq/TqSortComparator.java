package com.gy.tq;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class TqSortComparator extends WritableComparator {

    public TqSortComparator() {
        super(TqEntity.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        TqEntity tq1 = (TqEntity) a;
        TqEntity tq2 = (TqEntity) b;
        int c1 = Integer.compare(tq1.getYear(), tq2.getYear());
        if (c1 == 0) {
            int c2 = Integer.compare(tq1.getMonth(), tq2.getMonth());
            if (c2 == 0) {
                return -Integer.compare(tq1.getWd(), tq2.getWd());
            }
            return c2;
        }
        return c1;
    }
}
