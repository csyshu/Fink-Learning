package com.csy.flink.scala.training

import scala.util.matching.Regex

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/10/31 18:11
 */
object PatternTest {
  def main(args: Array[String]): Unit = {
    val pattern = "Scala".r
    val str = "Scala is Scalable and cool"
    println(pattern findFirstIn str)
    // 首字母可以是大写 S 或小写 s
    val pattern1 = new Regex("(S|s)cala")
    val str1 = "Scala is scalable and cool"
    // 使用逗号 , 连接返回结果
    println((pattern1 findAllIn str1).mkString(","))

    val pattern2 = new Regex("(S|s)cala")
    val str2 = "Scala is scalable and cool"
    println(pattern2 replaceFirstIn(str2, "Java"))
  }

}
