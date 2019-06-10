package com.gy.tq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class TqPartitioner extends Partitioner<TqEntity, IntWritable> {

    @Override
    public int getPartition(TqEntity tqEntity, IntWritable intWritable, int numPartitions) {
        return Integer.valueOf(tqEntity.getYear()).hashCode() % numPartitions;
    }
}
