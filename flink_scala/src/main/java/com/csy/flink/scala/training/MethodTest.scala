package com.csy.flink.scala.training

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/10/31 11:22
 */
object MethodTest {
  def f(c: Int) {
    println(c + 1)
  }

  var a = "sss";
  a = "ddd";

  def f1(c: Int): Unit = println(c + 5)

  def f2(): Long = {
    println("生成时间")
    val l = System.nanoTime()
    l
  }

  def f3: String = "3"

  f1(3: Int)
  f(2)
  //lambda表达式
  val x: Int => Unit = (c: Int) => println(c * 2)
  x.apply(2)

  def test(num: => Long): Unit = {
    println(num)
    num
  }

  test(f2())
  private val f4: String = f3

  def main(args: Array[String]): Unit = {

  }
}
