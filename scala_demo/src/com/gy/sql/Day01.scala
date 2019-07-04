package com.gy.sql

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object Day01 {

  var filename: String = "./scala_demo/jsons"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val ctx = new SparkContext(conf)
    val sql = new SQLContext(ctx)

    val df: DataFrame = sql.read.json(filename)
    df.show()
    df.printSchema()

    df.registerTempTable("temp")

    sql.sql("select lan,lng,time as save_time from temp ").show()
    sql.sql("select * from temp ").show(false)

    ctx.stop()

  }

}
