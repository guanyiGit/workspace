package com.gy.wc

import org.apache.spark.{SparkConf, SparkContext}

object SparkWordCount {

  val fileName = "./scala_demo/words"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("aparkWc")
    val sc = new SparkContext(conf)
    simpleWC(sc)
    sc.stop()

  }

  def simpleWC(sc: SparkContext): Unit = {
      sc.textFile(fileName).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).foreach(println)
  }

  /**
    * 普通wc
    */
  def commonWC(sc: SparkContext): Unit = {
    val lines = sc.textFile(fileName)
    val words = lines.flatMap(x => x.split(" "))
    val pairWords = words.map(x => (x, 1))
    val result = pairWords.reduceByKey((x: Int, y: Int) => x + y)
    result.foreach(println)
  }


}
