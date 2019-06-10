package com.gy.fof;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FofMapper extends Mapper<LongWritable, Text, Text, IntWritable> {


    Text mKey = new Text();
    IntWritable mVal = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //tom hello hadoop cat
        String[] tokenStrs = value.toString().split(" ");
        //直接好友0 间接好友1
        for (int i = 1; i < tokenStrs.length; i++) {
            mKey.set(getFof(tokenStrs[0], tokenStrs[i]));
            mVal.set(0);
            context.write(mKey, mVal);

            for (int j = i + 1; j < tokenStrs.length; j++) {
                mKey.set(getFof(tokenStrs[i], tokenStrs[j]));
                mVal.set(1);
                context.write(mKey, mVal);
            }
        }
    }

    private static String getFof(String s1, String s2) {
        if (s1.compareTo(s2) <= 0) {
            return s1 + ":" + s2;
        }
        return s2 + ":" + s1;
    }
}
