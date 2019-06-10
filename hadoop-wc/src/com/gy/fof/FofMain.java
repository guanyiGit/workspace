package com.gy.fof;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/*
tom hello hadoop cat
world hadoop hello hive
cat tom hive
mr hive hello
hive cat hadoop world hello mr
hadoop tom hive world
hello tom world hive mr

    hdfs dfs -mkdir -p /usr/fof/input
    hdfs dfs -put fof_data /usr/fof/input
    hdfs dfs -ls /usr/fof/input

    hadoop jar TqMain.jar com.gy.fof.FofMain

    hdfs dfs -cat /usr/fof/output/*

 */
public class FofMain {

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        Path in = new Path("/usr/fof/input");
        FileInputFormat.addInputPath(job, in);

        Path out = new Path("/usr/fof/output");
        if(out.getFileSystem(conf).exists(out))
            out.getFileSystem(conf).delete(out, true);

        FileOutputFormat.setOutputPath(job, out);


        job.setMapperClass(FofMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(FofReducer.class);

        job.waitForCompletion(true);
    }
}
