package com.gy.transformations

import org.apache.spark.{SparkConf, SparkContext}


object Day02 {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("day02")
    val ctx = new SparkContext(conf)

//    val rdd = ctx.parallelize((1 until 100), 4)
    val rdd = ctx.makeRDD(Array((1,2),(3,4)),2)
    println(rdd.partitions.size)

    ctx.stop()
  }


}
