package com.csy.flink.scala.training

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @dete 2020/10/31 16:30
 */
object ForeachTest {
  def main(args: Array[String]): Unit = {
    //i  j 语法
    for (x <- 1 to 10) {
      println(x)
    }
    println("---------------------------------")
    //i until j 语法(不包含 j)
    for (x <- 11 until 20) {
      println(x)
    }
    println("---------------------------------")
    //按先后顺序嵌套执行
    for (a <- 0 to 3; b <- 0 to 3) {
      println("Value of a: " + a);
      println("Value of b: " + b);
    }
    println("---------------------------------")
    //list循环
    val numList = List(1, 2, 3, 4, 5, 6);
    for (a <- numList) {
      println("Value of a: " + a);
    }
    println("---------------------------------")
    for (a <- numList if a != 3; if a != 5) {
      println(a)
    }
    println("---------------------------------")
    val inclusive = 0 to 3
    for (m <- inclusive) {
      println("Value of m: " + m)
    }
    println("---------------------------------")
    val mList =for (a <- numList if a != 3; if a != 5) yield a
    for (a <- mList)println(a)
  }
}
