package com.gy.sql

import org.apache.spark.sql.types.{DataTypes, StructField}
import org.apache.spark.sql.{RowFactory, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object Udf {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("udf").setMaster("local")
    val sc = new SparkContext(conf)

    val sql = new SQLContext(sc)
    val rdd1 = sc.parallelize(Array[String]("zhangsan", "lisi", "wangwu"))
    val rowRdd = rdd1.map(RowFactory.create(_))

    val rowSchema = Array[StructField](DataTypes.createStructField("name", DataTypes.StringType, true))
    val structType = DataTypes.createStructType(rowSchema)

    val df = sql.createDataFrame(rowRdd, structType)
    df.show(false)

    df.registerTempTable("temp")

    sql.udf.register("strLen", (x: String) => x.length())
    sql.udf.register("strLenInput", (x: String, y: Int) => if (x.length() != null && x.length() > 0) x.length() + y else y)

    val result = sql.sql("select name,strLen(name) as len,strLenInput(name,2) as len2 from temp")
    result.printSchema()
    result.show(false)

    sc.stop()
  }

}
