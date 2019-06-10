package com.gy.fof.step2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FofStep2Reducer extends Reducer<FoFEntity, Text, Text, IntWritable> {


    Text rKey = new Text();
    IntWritable rVal = new IntWritable();

    @Override
    protected void reduce(FoFEntity key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // (hive,1) hive:hdfs:1
        // (hive,3) hive:hadoop:3

        int i = 0;
        int oldScore = -1;
        for (Text k : values) {
            if (oldScore >= key.getScore()) {
                break;
            }
            String[] vs = k.toString().split(":");
            rKey.set(vs[0] + ":" + vs[1]);
            rVal.set(key.getScore());
            context.write(rKey, rVal);

            oldScore = key.getScore();
        }
    }
}
