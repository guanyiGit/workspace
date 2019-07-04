package com.gy.broad

import org.apache.spark.{SparkConf, SparkContext}


object AccumulatorTest {

  val fileName = "./scala_demo/words"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(" Accumulator").setMaster("local")
    val ctx = new SparkContext(conf)

    var i = ctx.accumulator(0)
    ctx.textFile(fileName).map(x => {
      i.add(1)
      x
    }).count()

    println(i.value)


    ctx.stop()
  }

}
