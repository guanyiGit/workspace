package com.gy.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class Day01 {

    static String fileName = "./Spark_demo/json";

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
        JavaSparkContext ctx = new JavaSparkContext(conf);

        SQLContext sqlContext = new SQLContext(ctx);
        DataFrame df = sqlContext.read().format("json").load(fileName);

        df.printSchema();
        df.show();

        df.select(
                df.col("name"), df.col("age"))
                .where(df.col("age").gt(18)).show();


        df.registerTempTable("tempTab");

        sqlContext.sql("select * from tempTab where age  > 0").show();

        ctx.stop();

    }

}
