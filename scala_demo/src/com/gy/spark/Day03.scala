package com.gy.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object Day03 {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val ctx = new SparkContext(conf)

    //    val rdd1 = ctx.parallelize(List[String](
    //      "love1", "love2", "love3", "love4",
    //      "love5", "love6", "love7", "love8",
    //      "love9", "love10", "love11", "love12"
    //    ), 3)
    //
    //    val rdd2 = rdd1.mapPartitionsWithIndex((index, iter) => {
    //      val list = new ListBuffer[String]()
    //      while (iter.hasNext) {
    //        val value = iter.next()
    //        list.+=(s"rdd1 partition =【$index】,value = 【$value】")
    //      }
    //      list.iterator
    //    })
    //
    ////    rdd2.foreach(println)
    //
    ////    val rdd3 = rdd2.repartition(4)
    //    val rdd3 = rdd2.coalesce(5,true)
    //
    //    val rdd4 = rdd3.mapPartitionsWithIndex((index, iter) => {
    //      val list = new ListBuffer[String]()
    //      while (iter.hasNext) {
    //        val value = iter.next()
    //        list.+=(s"rdd3 partition =【$index】,value = 【$value】")
    //      }
    //      list.iterator
    //    })
    //
    //    rdd4.foreach(println)


    val rdd1 = ctx.parallelize(List[String]("张三", "李四", "王五", "赵六"))
    val rdd2 = ctx.parallelize(List[String]("100", "200", "300", "400"))

    //    val rdd3 = rdd1.zip(rdd2)
    //    rdd3.foreach(println)

    val rdd3: RDD[(String, Long)] = rdd1.zipWithIndex()
    //    rdd3.foreach(println)


    ctx.parallelize(List[Int](1, 2, 3, 4, 5, 6, 5)).countByValue().foreach(println)
    ctx.parallelize(List[Tuple2[String,Int]](
      ("zhangsan", 12),
      ("zhangsan2", 12),
      ("zhangsan1", 12),
      ("zhangsan1", 12)
    )).countByKey().foreach(println)

    ctx.stop()
  }

}
