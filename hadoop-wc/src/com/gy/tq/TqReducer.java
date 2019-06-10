package com.gy.tq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TqReducer extends Reducer<TqEntity, IntWritable, Text, IntWritable> {

    Text rKey = new Text();
    IntWritable rVal = new IntWritable();


    @Override
    protected void reduce(TqEntity key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        //2004 01 01  88  -> 88
        //2004 01 12  79  -> 79

        for (IntWritable x : values) {
            int flag = 0;
            int dayF = 0;
            if (flag == 0) {
                rKey.set(key.getYear() + "-" + key.getMonth() + "-" + key.getDay() + "=>" + key.getWd());
                rVal.set(x.get());
                context.write(rKey, rVal);
                flag++;
                dayF = key.getDay();
            } else if (dayF != key.getDay()) {
                rKey.set(key.getYear() + "-" + key.getMonth() + "-" + key.getDay() + "=>" + key.getWd());
                rVal.set(x.get());
                context.write(rKey, rVal);
                return;
            }
        }
    }

}
