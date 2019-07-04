package com.gy.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object Day01 {

  case class PersonEMP(var id: String, var name: String, var age: Int)

  var filename: String = "./scala_demo/jsons"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val ctx = new SparkContext(conf)
    val sql: SQLContext = new SQLContext(ctx)

    //    example1(sql)
    //    example2(sql, ctx)
    example3(sql, ctx)

    ctx.stop()

  }


  def example3(sql: SQLContext, ctx: SparkContext): Unit = {
    val rdd: RDD[String] = ctx.textFile("./scala_demo/json2")
    val perSonRdd: RDD[PersonEMP] = rdd.map(x => {
      val splits = x.split(",")
      new PersonEMP(splits(0), splits(1), splits(2).toInt)
    })
    val df: DataFrame = sql.createDataFrame(perSonRdd)
    df.printSchema()
    df.show(false)

  }

  def example2(sql: SQLContext, ctx: SparkContext): Unit = {
    val array: Array[String] = Array[String](
      "{\"lng\":\"113.891201\",\"lan\":\"22.567443\",\"time\":\"2019-07-03 21:42:27.030\"}",
      "{\"lng\":\"113.891201\",\"lan\":\"22.567443\",\"time\":\"2019-07-03 21:42:27.030\"}",
      "{\"lng\":\"113.891201\",\"lan\":\"22.567443\",\"time\":\"2019-07-03 21:42:27.030\"}"
    )
    val rdd1: RDD[String] = ctx.parallelize(array)
    val df = sql.read.json(rdd1)
    df.printSchema()
    df.show(false)
  }

  def example1(sql: SQLContext): Unit = {
    val df: DataFrame = sql.read.json(filename)
    df.printSchema()
    df.registerTempTable("temp")
    sql.sql("select lan,lng,time as save_time from temp ").show()
    df.select(df.col("lan"), df.col("lng"), df.col("time")).where(df.col("lan").gt(0)).show(false)
    val rowRdd: RDD[Row] = df.rdd
    rowRdd.foreach(x => {
      val lan = x.get(0)
      val lng = x.getAs[String]("lng")
      val time = x.getAs[String]("time")
      println(s"lan=${lan},lng=${lng},time=${time}")
      println(x)
    })

  }

}
