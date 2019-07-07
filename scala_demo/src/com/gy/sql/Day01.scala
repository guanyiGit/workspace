package com.gy.sql

import java.util.Properties

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Day01 {

  case class PersonEMP(var id: String, var name: String, var age: Int)

  var filename: String = "./scala_demo/jsons"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    conf.set("spark.sql.shuffle.partitions", "1")
    val ctx = new SparkContext(conf)
    val sql: SQLContext = new SQLContext(ctx)

    //    example1(sql)
    //    example2(sql, ctx)
    example6(sql, ctx)

    ctx.stop()

  }


  /**
    * 读取数据库
    *
    * @param sql
    * @param ctx
    */
  def example6(sql: SQLContext, ctx: SparkContext): Unit = {
    val options = new mutable.HashMap[String, String]()
    options.put("url", "jdbc:mysql://129.211.79.98:3306/spark")
    options.put("driver", "com.mysql.jdbc.Driver")
    options.put("user", "root")
    options.put("password", "123456")
    options.put("dbtable", "person")
    val df = sql.read.format("jdbc").options(options).load()
    df.show()
    df.registerTempTable("ptab")


    val reader = sql.read.format("jdbc")
    reader.option("url", "jdbc:mysql://129.211.79.98:3306/spark")
    reader.option("driver", "com.mysql.cj.jdbc.Driver")
    reader.option("user", "root")
    reader.option("password", "123456")
    reader.option("dbtable", "score")
    val score = reader.load()
    score.registerTempTable("stab")
    score.show(false)
    score.printSchema()

    val result: DataFrame = sql.sql("select p.id,p.uname,p.age,s.score_num from ptab p,stab s where p.id = s.pid")
    result.show(false)


    val properties: Properties = new Properties()
    properties.setProperty("user", "root")
    properties.setProperty("password", "123456")

    result.write.mode(saveMode = SaveMode.Overwrite).jdbc("jdbc:mysql://129.211.79.98:3306/spark", "result", properties)

  }


  /**
    * 读取parquet文件
    *
    * @param sql
    * @param ctx
    */
  def example5(sql: SQLContext, ctx: SparkContext): Unit = {
    val df = sql.read.format("parquet").load("./scala_demo/parquet")
    df.show(false)
  }

  /**
    * 动态schema
    *
    * @param sql
    * @param ctx
    */
  def example4(sql: SQLContext, ctx: SparkContext): Unit = {
    val rdd: RDD[String] = ctx.textFile("./scala_demo/json2")

    val rowRdds: RDD[Row] = rdd.map(x => {
      val strs = x.split(",")
      RowFactory.create(strs(0), strs(1), Integer.valueOf(strs(2)))
    })
    val rowSchema = Array[StructField](
      DataTypes.createStructField("id", DataTypes.StringType, true),
      DataTypes.createStructField("name", DataTypes.StringType, true),
      DataTypes.createStructField("age", DataTypes.IntegerType, true)
    )
    val types: StructType = DataTypes.createStructType(rowSchema)

    val df: DataFrame = sql.createDataFrame(rowRdds, types)
    df.show()
  }

  /**
    * 读取java类反射生成
    *
    * @param sql
    * @param ctx
    */
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

  /**
    * 读取jsonrdd文件生成
    *
    * @param sql
    * @param ctx
    */
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

  /**
    * 读取json文件生成
    *
    * @param sql
    */
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
