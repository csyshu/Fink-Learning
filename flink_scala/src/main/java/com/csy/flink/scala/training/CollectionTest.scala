package com.csy.flink.scala.training

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/10/31 17:31
 */
object CollectionTest {
  def main(args: Array[String]): Unit = {
    val map1 = Map("key1" -> "value1")
    val maybeString = map1.get(key = "key1")
    maybeString.map(_.length).map("length:" + _).foreach(println)
    maybeString.map("length:" + _.length).foreach(println)
    val x: Option[Int] = None
    val value = x.getOrElse(Some(5))
    println(value)

    val ita = Iterator(20, 40, 2, 50, 69, 90)
    val itb = Iterator(20, 40, 2, 50, 69, 90)

    println("最大元素是：" + ita.max)
    println("最小元素是：" + itb.min)

  }

}
