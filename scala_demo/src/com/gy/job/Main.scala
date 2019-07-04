package com.gy.job

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Main {
  val fileName = "./scala_demo/pvuvdata"


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("tes").setMaster("local")
    val ctx = new SparkContext(conf)

    //14.36.206.127	河北	2018-03-29	1522294977304	2871969784239838688	www.gome.com.cn	Comment
    val liens = ctx.textFile(fileName)

    //    pv(liens)
//    uv(liens)
    site_location(liens = liens,n=10)

    ctx.stop()
  }

  def pv(liens: RDD[String]) = {
    liens.map(x => (x.split("\t")(5), 1)).reduceByKey((_ + _)).sortBy((x) => x._2, false).take(100).foreach(println)
  }

  def uv(liens: RDD[String]) = {
    liens.map(x => x.split("\t")(0) + "_" + x.split("\t")(5))
      .distinct()
      .map(x => (x.split("_")(1), 1))
      .reduceByKey(_ + _)
      .sortBy(x => x._2, false)
      .foreach(println)
    //    liens.map(x => (x.split("\t")(4), 1)).reduceByKey(_ + _).sortBy(_._2, false).take(50).foreach(println)
  }


  //14.36.206.127	河北	2018-03-29	1522294977304	2871969784239838688	www.gome.com.cn	Comment
  def topn(ctx: SparkContext, liens: RDD[String], n: Int = 2) = {
  }


  def site_location( liens: RDD[String], n: Int = 5) = {
    liens.map(x => (x.split("\t")(5), x.split("\t")(1))).groupByKey().map(x => {
      val localMap = mutable.Map[String, Int]();
      val site: String = x._1
      val localIts = x._2.iterator
      while (localIts.hasNext) {
        val local = localIts.next()
        if (localMap.contains(local)) {
          localMap.put(local, localMap.get(local).get + 1)
        } else {
          localMap.put(local, 1)
        }
      }
      val newList = localMap.toList.sortBy(-_._2).take(n)
      (site,newList.toBuffer)
    }).foreach(println)
  }
}
