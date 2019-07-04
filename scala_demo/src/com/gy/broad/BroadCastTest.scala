package com.gy.broad

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}

object BroadCastTest {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("croad").setMaster("local")
    val ctx = new SparkContext(conf)

    val list = List[String]("zs", "ls")
    val bcList: Broadcast[List[String]] = ctx.broadcast(list)
    ctx.parallelize(List[String]("zs", "ls", "wangwu"))
      .filter(x => {
        val innerList: List[String] = bcList.value
        !innerList.contains(x)
      }).foreach(println)


    ctx.stop()
  }

}
