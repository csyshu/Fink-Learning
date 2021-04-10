package com.csy.flink.scala.training

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/11/11 19:55
 */
object KeyByTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.generateSequence(1, 100).map(new MapFunction[java.lang.Long, (java.lang.Long, Int)] {
      override def map(t: java.lang.Long): (java.lang.Long, Int) = {
        (t, 1)
      }
    }).keyBy(new KeySelector[(java.lang.Long, Int), java.lang.Long] {
      override def getKey(in: (java.lang.Long, Int)): java.lang.Long = {
        in._1
      }
    })
      .sum(1)
    print()
    env.execute()
  }

}
