package com.gy.fof;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FofReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    IntWritable rVal = new IntWritable(-1);

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        //hadoop:hive 0
        //hadoop:hive 1

        int flag = 0;
        int sum = 0;
        for (IntWritable x : values) {
            if (x.get() == 0) {
//                rVal.set(-1);
//                context.write(key, rVal);
                flag = 1;
            }
            sum += x.get();
        }

        if (flag == 0) {
            rVal.set(sum);
            context.write(key, rVal);
        }

    }

}
