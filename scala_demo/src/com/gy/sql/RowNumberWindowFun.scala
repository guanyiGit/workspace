package com.gy.sql

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * row_number()开窗函数
  * 主要是按照耨个字段分组 最后取另一个字段前几个  分组topN
  * 注意:如果sql语句连使用到了开窗函数 那么这个sql语句必须使用hivecontext执行
  */
/*
spark-submit --master spark://centos98:7077 --class com.gy.sql.RowNumberWindowFun /root/test/sparkOnHive.jar
 */
object RowNumberWindowFun {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("RowNumberWindowFun")
    conf.set("spark.sql.shuffle.partitions", "1")
    val sc = new SparkContext(conf)
    val sql = new HiveContext(sc)

    sql.sql("use spark")
    sql.sql("drop table if exists sales")
    sql.sql("create table if not exists sales (riqi string,leibie string,jine int)  row format delimited fields terminated by '\\t'")
    sql.sql("load data local inpath '/root/test/sales' into table sales")

    /**
      * rank从1开始
      */
    val result = sql.sql("select *  from (select riqi,leibie,jine,row_number() over (partition by leibie order by jine desc) rank from sales) t where t.rank <=3")

    result.show(100, false)
    result.write.mode(saveMode = SaveMode.Overwrite).saveAsTable("sales_result")

    sc.stop()
  }

}
