package com.gy.fof.step2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/*
cat:hadoop      2
cat:hello       2
cat:mr  1
cat:world       1
hadoop:hello    3
hadoop:mr       1
hive:tom        3
mr:tom  1
mr:world        2
tom:world       2

hdfs dfs -cat /usr/fof/output/step2/part-r-00000

 */
public class FofMainStep2 {

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        conf.set("mapreduce.app-submission.corss-paltform", "true");
        //如果分布式运行,必须打jar包
        //且,client在集群外非hadoop jar 这种方式启动,client中必须配置jar的位置

        conf.set("mapreduce.framework.name", "local");
        //这个配置,只属于,切换分布式到本地单进程模拟运行的配置
        //这种方式不是分布式,所以不用打jar包


        Path in = new Path("/usr/fof/output/part-r-00000");
        FileInputFormat.addInputPath(job, in);

        Path out = new Path("/usr/fof/output/step2");
        if (out.getFileSystem(conf).exists(out))
            out.getFileSystem(conf).delete(out, true);

        FileOutputFormat.setOutputPath(job, out);


        job.setMapperClass(FofMapperStep2.class);
        job.setMapOutputKeyClass(FoFEntity.class);
        job.setMapOutputValueClass(Text.class);
        job.setSortComparatorClass(FofStep2SortComparator.class);


        job.setGroupingComparatorClass(FofStep2GroupingComparator.class);
        job.setReducerClass(FofStep2Reducer.class);

        job.waitForCompletion(true);
    }
}
