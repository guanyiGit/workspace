package com.gy.streaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * nc -lk 6666
  */
object SparkStreamingTest {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("streamingTest")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, batchDuration = Durations.seconds(5l))

    /**
      * 如果你的batchInterval小于10s 那么10s会将内存中的数据写入磁盘一次
      * 如果你的batchInterval大于10s 那么就一batchIntvartel为准
      */
    ssc.checkpoint("./scala_demo/straming/checkpoint")

    val rid: ReceiverInputDStream[String] = ssc.socketTextStream("centos98", 6666)

    val result: DStream[(String, Int)] = rid.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    result.print(100)
    result.foreachRDD(x => {
      val context = x.context
      val broadcast = context.broadcast("广播变量，可以从文件或者数据库中读取，driver端会定时执行，不需要触发动作")
      println(broadcast.value)
      x.map(y => (y._1 + "~", y._2)).foreach(println)
    })

    /**
      * params1 按照key统计的所有value
      * params2 按照key统计的之前的值
      */
    val ds: DStream[(String, Int)] = result.updateStateByKey((x: Seq[Int], y: Option[Int]) => {
      var rt = 1;
      if (y.nonEmpty) {
        rt = y.get
      }
      for (ele <- x) {
        rt = rt + ele
      }
      Option.apply[Int](rt)
    })

    ds.print(100)


    ssc.start()
    ssc.awaitTermination()
  }

}
