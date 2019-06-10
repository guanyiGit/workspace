package com.gy.tq;

/*
1949-10-01 14:21:02	34c
1949-10-01 19:21:02	38c
1949-10-02 14:01:02	36c
1950-01-01 11:21:02	32c
1950-10-01 12:21:02	37c
1951-12-01 12:21:02	23c
1950-10-02 12:21:02	41c
1950-10-03 12:21:02	27c
1951-07-01 12:21:02	45c
1951-07-02 12:21:02	46c
1951-07-03 12:21:03	47c
1949-10-01 14:21:02	34c
1949-10-01 19:21:02	38c
1949-10-02 14:01:02	36c
1950-01-01 11:21:02	32c
1950-10-01 12:21:02	37c
1951-12-01 12:21:02	23c
1950-10-02 12:21:02	41c
1950-10-03 12:21:02	27c
1951-07-01 12:21:02	45c
1951-07-02 12:21:02	46c
1951-07-03 12:21:03	47c

    hdfs dfs -mkdir -p /usr/tq/input
    hdfs dfs -put tq_data /usr/tq/input
    hdfs dfs -ls /usr/tq/input

    hadoop jar TqMain.jar com.gy.tq.TqMain

    hdfs dfs -cat /usr/tq/output/*
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 天气案例
 * 需求：找出每个月气温最高的2天
 */
public class TqMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration(true);

        //conf===============================
        //map start ===========================
        Job job = Job.getInstance(conf);
        job.setJarByClass(TqMain.class);

//        job.setInputFormatClass();
        job.setMapperClass(TqMapper.class);

        job.setMapOutputKeyClass(TqEntity.class);

        job.setMapOutputValueClass(IntWritable.class);

        job.setPartitionerClass(TqPartitioner.class);

        job.setSortComparatorClass(TqSortComparator.class);

//        job.setCombinerClass(TqCombiner.class);
        //map end ===========================

        //reduce start ===========================
        job.setGroupingComparatorClass(TqGroupingComparator.class);
        job.setReducerClass(TqReducer.class);

        //reduce end ===========================

        Path in = new Path("/usr/tq/input");
        FileInputFormat.addInputPath(job, in);

        Path out = new Path("/usr/tq/output");
        if (out.getFileSystem(conf).exists(out))
            out.getFileSystem(conf).delete(out, true);
        FileOutputFormat.setOutputPath(job, out);

        job.setNumReduceTasks(2);
        //conf===============================
        job.waitForCompletion(true);
    }
}
