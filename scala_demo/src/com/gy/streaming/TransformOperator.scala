package com.gy.streaming

import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object TransformOperator {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TransformOperator").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, batchDuration = Durations.seconds(5l))

    //黑名单
    val list = Array[String]("zhangsan")
    val bcBlackList = ssc.sparkContext.broadcast(list)

    val ds = ssc.socketTextStream("centos98", 6666)
    val mapDs = ds.map(x => (x.split(" ")(1), x))
    mapDs.transform((x) => {
      println("driver端执行的。。。")
      x.filter(y => !bcBlackList.value.contains(y._1)).map(y => y._2)
      x
    }).print(200)


    ssc.start()
    ssc.awaitTermination()

  }

}
