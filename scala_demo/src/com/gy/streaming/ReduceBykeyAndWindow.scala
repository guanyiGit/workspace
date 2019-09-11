package com.gy.streaming

import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object ReduceBykeyAndWindow {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ReduceBykeyAndWindow").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val ssc = new StreamingContext(sc, batchDuration = Durations.seconds(5l))
    ssc.checkpoint("./scala_demo/ReduceBykeyAndWindow/checkpoint")
    val ds = ssc.socketTextStream("centos98", 6666)
    val tuple = ds.flatMap(_.split(" ")).map((_, 1))

    /**
      * 每隔5s计算最近15s的数据  必须需是整数倍
      */
    val result = tuple.reduceByKeyAndWindow(reduceFunc = (x, y) => {
      x + y
    }, windowDuration = Durations.seconds(15l), slideDuration = Durations.seconds(5l))
    result.print()

    /**
      * 必须要checkpoint
      * params1 进来的数据
      * params2 出去的数据
      * params3 窗口大小
      * params4 滑动间隔
      */
    val result2 = tuple.reduceByKeyAndWindow(reduceFunc = (x, y) => {
      x + y
    }, invReduceFunc = (x, y) => {
      x - y
    }, windowDuration = Durations.seconds(15l), slideDuration = Durations.seconds(5l))

    result2.print(100)

    ssc.start()
    ssc.awaitTermination()

  }

}
