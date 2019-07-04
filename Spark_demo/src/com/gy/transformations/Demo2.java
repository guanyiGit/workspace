package com.gy.transformations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class Demo2 {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
        JavaSparkContext ctx = new JavaSparkContext(conf);
        ctx.setLogLevel("WARN");

        JavaPairRDD<String, String> pairRDD1 = ctx.parallelizePairs(Arrays.asList(
                new Tuple2<String, String>("zhansan", "a"),
                new Tuple2<String, String>("lisi", "b")
        ), 2);

        JavaPairRDD<String, String> pairRDD2 = ctx.parallelizePairs(Arrays.asList(
                new Tuple2<String, String>("zhansan", "11111"),
                new Tuple2<String, String>("zhansan2", "c"),
                new Tuple2<String, String>("lisi2", "d")
        ), 4);

        JavaPairRDD<String, Tuple2<String, String>> join = pairRDD1.join(pairRDD2);
        join.foreach(x -> {
            System.out.println(x);
        });
        System.out.println(join.partitions().size() );

        System.out.println("------------------------------------------------------------------");

        pairRDD1.leftOuterJoin(pairRDD2).foreach(x-> System.out.println(x));


        ctx.stop();
    }
}
