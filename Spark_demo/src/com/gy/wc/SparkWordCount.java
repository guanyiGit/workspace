package com.gy.wc;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Arrays;

public class SparkWordCount {

    static String fileName = "./scala_demo/words";

    public static void main(String[] args) {

        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("javaSparkWC");
        JavaSparkContext ctx = new JavaSparkContext(conf);


        simpleWC(ctx);
//        commonWC(ctx);



        ctx.stop();
    }

    private static void simpleWC(JavaSparkContext ctx) {
        ctx.textFile(fileName)
                .flatMap(x -> Arrays.asList(x.split(" ")))
                .mapToPair(x -> new Tuple2<>(x, 1))
                .reduceByKey((x, y) -> x + y)
                .foreach(x->System.out.println(x));
    }

    private static void commonWC(JavaSparkContext ctx) {
        JavaRDD<String> lines = ctx.textFile(fileName);

        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(String line) throws Exception {
                return Arrays.asList(line.split(" "));
            }
        });

        JavaPairRDD<String, Integer> wordPair = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<>(word, 1);
            }
        });

        JavaPairRDD<String, Integer> result = wordPair.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer oldVal, Integer newVal) throws Exception {
                return oldVal + newVal;
            }
        });

        result.foreach(new VoidFunction<Tuple2<String, Integer>>() {
            @Override
            public void call(Tuple2<String, Integer> tuple2) throws Exception {
                System.out.println("(" + tuple2._1 + "," + tuple2._2 + ")");
            }
        });
    }


}
