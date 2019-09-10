package com.gy.tfidf;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * c1_001,2 c2_001,1 count,10000
 * 
 * @author root
 *
 */
public class FirstReduce extends Reducer<Text, IntWritable, Text, IntWritable> {

	protected void reduce(Text key, Iterable<IntWritable> iterable,
			Context context) throws IOException, InterruptedException {

		/*
		今天_3823890210294392	1
		今天_3823890210294392	1
		count 1
		count 1
		count 1
		...


		 */
		int sum = 0;
		for (IntWritable i : iterable) {
			sum = sum + i.get();
		}
		if (key.equals(new Text("count"))) {
			System.out.println(key.toString() + "___________" + sum);
		}
		context.write(key, new IntWritable(sum));
		//今天_3823890210294392	2
		//count	100
	}
}
