package com.gy.lineLogic

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("lineML").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")

    val data = sc.textFile("ML/data/lpsa.data")
    val example: RDD[LabeledPoint] = data.map(x => {
      val parts = x.split(",")
      val y = parts(0)
      val x_list = parts(1)
      new LabeledPoint(y.toDouble, Vectors.dense(x_list.split(" ").map(_.toDouble)))
    })

    val tranData: Array[RDD[LabeledPoint]] = example.randomSplit(Array(0.8, 0.2), 1l)

    /**
      * 迭代次数
      * 训练一个多元性回归模型收敛（停止迭代）条件：
      * 1、error值小于用户制定的error值
      * 2、达到一定的迭代次数
      */
    val numInterations = 100

    /**
      * 迭代步长  梯度下降算法的下降步长大小
      */
    val step = 1

    /**
      *
      */
    val miniBatchFraction = 1

    /**
      * 梯度下降的方法求误差函数
      */
    val lrs = new LinearRegressionWithSGD()

    //让训练出来的额模型有截距
    lrs.setIntercept(true)

    //步长
    lrs.optimizer.setStepSize(step)

    //迭代次数
    lrs.optimizer.setNumIterations(numInterations)

    //每一次下山后，是否计算所有样本的误差值,1代表所有样本,默认就是1.0
    lrs.optimizer.setMiniBatchFraction(miniBatchFraction)

    /**
      * 训练模型  使用训练集  0.8
      */
    val model = lrs.run(tranData(0))
    println(s"weights:${model.weights}")
    println(s"intercept:${model.intercept}")

    /**
      * 测试模型  用向量的值去预测
      */
    val prediction: RDD[Double] = model.predict(tranData(1).map(_.features))


    val predictionAndLabel: RDD[(Double, Double)] = prediction.zip(tranData(1).map(_.label))

    val print_predict = predictionAndLabel.take(20)
    for (i <- 0 to print_predict.length - 1) {
      println("预测值:" + print_predict(i)._1 + "\t实际值：" + print_predict(i)._2 + "\t差值:" + (print_predict(i)._2 - print_predict(i)._1))

    }

    //计算测试集总误差数
    val loss = predictionAndLabel.map {
      case (p, v) =>
        val err = p - v
        Math.abs(err)
    }.reduce(_ + _)

    /**
      * 测试集误差值
      */
    val error = loss / tranData(1).count
    println("Test RMSE = " + error + "\ttotal error:" + loss)
    // 模型保存
    //    val ModelPath = "model"
    //    model.save(sc, ModelPath)
    //    val sameModel = LinearRegressionModel.load(sc, ModelPath)
    sc.stop()

  }

}
