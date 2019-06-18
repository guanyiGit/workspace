package com.gy

object Main {

  def main(args: Array[String]): Unit = {
    val s1 = "你好";
    val s2 = "hello";

    println(s1.equals(s2))

    val sb = new StringBuilder();
    sb.+('a');
    sb.++=("b");
    sb.append("c");
    println(sb);



    val arr = Array[Any](1, 'a', "hello", true);

    arr.foreach(println)


    val arr1 = new Array[Int](3);
    arr1(0) = 1
    arr1(1) = 2
    arr1(2) = 4

    for (temp <- arr1) {
      println(temp)
    }
    println("List----------------------")

    val list = List[AnyVal](1, 2, 3, 4);

    for (temp <- list) {
      println(temp)
    }
  }

}
