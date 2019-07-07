package com.gy.sql

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * hive:
  * create database spark;
  * spark-submit --master spark://centos98:7077 --class com.gy.sql.CreateDfFromHive /root/test/sparkOnHive.jar
  */
object CreateDfFromHive {


  def main(args: Array[String]): Unit = {
    val sc = cloud()
    sc.stop()
  }


  private def cloud(): SparkContext = {
    val conf = new SparkConf().setAppName("CreateDfFromHive")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)
    baseSql(hiveContext)
    return sc
  }

  private def baseSql(hiveContext: _root_.org.apache.spark.sql.hive.HiveContext) = {
    hiveContext.sql("use spark")
    hiveContext.sql("drop table if exists student_infos")
    hiveContext.sql("create table if not exists student_infos (name string,age int) row format delimited fields terminated by '\\t'")
    hiveContext.sql("load data local inpath '/root/test/student_infos' into table student_infos")

    hiveContext.sql("drop table if exists student_scores")
    hiveContext.sql("create table if not exists student_scores (name string,score int) row format delimited fields terminated by '\\t'")
    hiveContext.sql("load data local inpath '/root/test/student_scores' into table student_scores")

    val df = hiveContext.sql("select si.name,si.age,ss.score from student_infos si join student_scores ss on si.name=ss.name where ss.score >=80")

    df.registerTempTable("goodsTab")
    val result = hiveContext.sql("select * from goodsTab")
    result.show(false)

    hiveContext.sql("drop table if exists good_student_infos")
    result.write.mode(saveMode = SaveMode.Overwrite).saveAsTable("good_student_infos")
    val table = hiveContext.table("good_student_infos")
    table.collect().iterator.foreach(println)
  }
}
