package com.gy.pv;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PvWc {

    static String fileName = "./Spark_demo/pvuvdata";

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("pv").setMaster("local");
        JavaSparkContext ctx = new JavaSparkContext(conf);

        //143.37.220.4	广西	2018-03-29	1522294977336	7179606461665134009	www.jd.com	Login
        JavaRDD<String> lines = ctx.textFile(fileName);

        //www.jd.com  143.37.220.4	广西	2018-03-29	1522294977336	7179606461665134009	www.jd.com	Login
        JavaPairRDD<String, String> pairRDD = lines.mapToPair(x -> {
            String[] strs = x.split("\t");
            return new Tuple2<String, String>(strs[5], x);
        });

        //www.jd.com 18791
        Map<String, Object> byKey = pairRDD.countByKey();


//        JavaPairRDD<String, Long> mapToPair = pairRDD.mapToPair(x -> {
//            Optional<Object> first = byKey.entrySet().stream().filter(y -> y.getKey().equals(x._1)).map(y -> y.getValue()).findFirst();
//            Object count = first.get();
//            return new Tuple2<>(x._2, Long.valueOf(String.valueOf(count)));
//        });
//
//        mapToPair.sortByKey(false).foreach(x -> {
//            System.out.println("x:" + x);
//        });


        List<Tuple2<String, Long>> list = new ArrayList<>();
        byKey.entrySet().stream().forEach(x -> {
            String key = x.getKey();
            Object value = x.getValue();
            System.out.println("job1:" + key + "-->" + value);
            list.add(new Tuple2<>(key, Long.valueOf(String.valueOf(value))));
        });


        JavaRDD<Tuple2<String, Long>> parallelize = ctx.parallelize(list);
        JavaPairRDD<Long, String> pairRDD1 = parallelize.mapToPair(x -> x.swap());

        JavaPairRDD<Long, String> pairRDD2 = pairRDD1.sortByKey(false);
        JavaPairRDD<String, Long> resultRdd = pairRDD2.mapToPair(x -> x.swap());
        resultRdd.foreach(x -> {
            System.out.println("job2:" + x);
        });

        ctx.stop();
    }
}

