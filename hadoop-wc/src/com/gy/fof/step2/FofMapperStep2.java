package com.gy.fof.step2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FofMapperStep2 extends Mapper<LongWritable, Text, FoFEntity, Text> {


    FoFEntity mKey = new FoFEntity();
    Text mVal = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //hive:tom        3

        String[] tokenStrs = value.toString().split("\t");
        String[] names = tokenStrs[0].split(":");
        int num =Integer.valueOf( tokenStrs[1]);

        mKey.setUname(names[0]);
        mKey.setScore(num);
        mVal.set(names[0]+":"+names[1]+":"+num);
        context.write(mKey, mVal);

        mKey.setUname(names[1]);
        mKey.setScore(num);
        mVal.set(names[1]+":"+names[0]+":"+num);
        context.write(mKey, mVal);

    }

}
