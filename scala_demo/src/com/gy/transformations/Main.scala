package com.gy.transformations

import org.apache.spark.{SparkConf, SparkContext}

object Main {


  val fileName = "./scala_demo/words"


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val ctx = new SparkContext(conf)

    val lines = ctx.textFile(fileName)
    lines.sample(true,0.5)
        .foreach(println)

    ctx.stop()
  }

}
