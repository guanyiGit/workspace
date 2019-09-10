package com.gy.tfidf;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TwoReduce extends Reducer<Text, IntWritable, Text, IntWritable> {

	protected void reduce(Text key, Iterable<IntWritable> arg1, Context context)
			throws IOException, InterruptedException {
		//豆浆 1
		//豆浆 1
		//...
		int sum = 0;
		for (IntWritable i : arg1) {
			sum = sum + i.get();
		}
		//豆浆 100
		context.write(key, new IntWritable(sum));
	}

}
