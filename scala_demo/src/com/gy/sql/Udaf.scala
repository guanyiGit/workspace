package com.gy.sql

import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DataTypes, StructField, StructType}
import org.apache.spark.sql.{Row, RowFactory, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object Udaf {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("udaf").setMaster("local")
    conf.set("spark.sql.shuffle.partitions", "1")
    val sc = new SparkContext(conf)
    val sql = new SQLContext(sc)

    val rdd1 = sc.parallelize(Array[String]("zhangsan", "zhangsan", "lisi", "lisi", "wangwu", "zhangsan"))
    val rowRdd = rdd1.map(RowFactory.create(_))

    val rowSchema = Array[StructField](DataTypes.createStructField("name", DataTypes.StringType, true))
    val structType = DataTypes.createStructType(rowSchema)

    val df = sql.createDataFrame(rowRdd, structType)
    df.show(false)

    df.registerTempTable("temp")

    val udaf = new UserDefinedAggregateFunction {
      /**
        * 初始化一个内部的自已定义的值 在aggregate之前每组数据的初始化结果
        *
        * @param buffer
        */
      override def initialize(buffer: MutableAggregationBuffer): Unit = {
        buffer.update(0, 0)
      }

      /**
        * 更新  可以认为一个一个地将组内的字段值传递进来 实现拼接的逻辑
        * buffer.getInt(0) 获取的上一个聚合后的值
        * 相当于map端的combiner conbiner就是对每一个map task的处理结果进行一次小聚合
        * 大聚合发生在reduce端
        * 这里既是：在进行聚合的时候 每当有新的值进来 对分组后的聚合如何进行计算
        *
        * @param buffer
        * @param row
        */
      override def update(buffer: MutableAggregationBuffer, row: Row): Unit = {
        println(s"Row=${row}")
        buffer.update(0, buffer.getInt(0) + 1)
      }

      /**
        * 合并update操作 可能是针对一个分组内的部分数据 在某个节点上发生的 但是可能一个分组内的数据 会分布在多个节点上处理
        * 此时就要用merge操作 讲每个节点上分布式拼接好的串 合并起来
        * buffer1.getInt(0):大聚合的时候上一次聚合的值
        * buffer2.getInt(0):这次计算传入进来的update的结果
        * 这里既是 最后在分布式节点完成后需要进行全局级别的merge操作
        *
        * @param buffer1
        * @param buffer2
        */
      override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
        buffer1.update(0, buffer1.getInt(0) + buffer2.getInt(0))
      }

      /**
        * 在进行聚合操作的时候所需要处理的数据的记过的类型
        *
        * @return
        */
      override def bufferSchema: StructType = {
        DataTypes.createStructType(Array[StructField](DataTypes.createStructField("bffer", DataTypes.IntegerType, true)))
      }

      /**
        * 最后返回一个datatype方法的类型要一直的裂隙 返回udaf租后的计算结果
        *
        * @param buffer
        * @return
        */
      override def evaluate(buffer: Row): Any = {
        buffer.getInt(0)
      }


      /**
        * 知道udaf函数计算后返回的结果类型
        *
        * @return
        */
      override def dataType: DataType = {
        DataTypes.IntegerType
      }

      /**
        * 制定输入字段的字段及类型
        *
        * @return
        */
      override def inputSchema: StructType = {
        DataTypes.createStructType(Array[StructField](DataTypes.createStructField("namexxx", DataTypes.StringType, true)))
      }

      /**
        * 确保一致性一般用true  用以标记针对的一组输入  udaf是否总是生成相同的结果
        *
        * @return
        */
      override def deterministic: Boolean = true


    }

    sql.udf.register("stringCount", udaf)

    val result = sql.sql("select name,stringCount(name) as strWc from temp group by name")
    result.printSchema()
    result.show(false)


    sc.stop()
  }

}
