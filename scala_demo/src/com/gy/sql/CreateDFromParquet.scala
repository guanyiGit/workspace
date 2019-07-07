package com.gy.sql

import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

object CreateDFromParquet {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("test")
    val sc = new SparkContext(conf)
    val sql = new SQLContext(sc)

    val df = sql.read.json("./scala_demo/jsons")
    df.write.mode(saveMode = SaveMode.Overwrite).format("parquet").save("./scala_demo/parquet")

    sql.read.parquet("./scala_demo/parquet").show(false)

    sc.stop()
  }

}
