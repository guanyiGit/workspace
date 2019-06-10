package com.gy.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/*
    hdfs dfs -mkdir -p /usr
    hdfs dfs -put  wc_data /usr
    hdfs dfs -ls /usr
    hadoop jar mywc.jar com.gy.wc.MyWC
    hdfs dfs -cat /usr/out/*
 */
public class MyWC {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Job job = Job.getInstance(conf);

        job.setJarByClass(MyWC.class);

        // Specify various job-specific parameters
        job.setJobName("wodeWC");

        Path in = new Path("/usr/wc_data");
        FileInputFormat.addInputPath(job, in);
        Path out = new Path("/usr/out/");
        if (out.getFileSystem(conf).exists(out))
            out.getFileSystem(conf).delete(out, true);
        FileOutputFormat.setOutputPath(job, out);

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.waitForCompletion(true);
    }


}
