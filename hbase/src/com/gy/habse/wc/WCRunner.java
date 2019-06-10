package com.gy.habse.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
/*
java hbase hive hive public
hbase java javase javaee
private public java hbase
hbase mysql java hbase

hdfs dfs -mkdir -p /usr/hbase/input
hdfs dfs -put hbase_data  /usr/hbase/input

hdfs dfs -cat /usr/hbase/input/*

create 'tab_wc','cf'


 */
public class WCRunner {

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://centos15:9000");
		conf.set("hbase.zookeeper.quorum", "centos15:2181");

		Job job = Job.getInstance(conf);
		job.setJarByClass(WCRunner.class);

		// 指定mapper 和 reducer
		job.setMapperClass(WCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		// 最后一个参数设置false  false不添加依赖jar ide中运行
		// TableMapReduceUtil.initTableReducerJob(table, reducer, job);
		TableMapReduceUtil.initTableReducerJob("tab_wc", WCReducer.class, job, null, null, null, null, false);
		FileInputFormat.addInputPath(job, new Path("/usr/hbase/input/"));
		job.waitForCompletion(true);
	}
}
