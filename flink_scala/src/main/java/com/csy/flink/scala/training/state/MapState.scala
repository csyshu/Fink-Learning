package com.csy.flink.scala.resource.scala.training.state

import org.apache.flink.api.common.functions.{FlatMapFunction, MapFunction, RichMapFunction}
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.util.Collector

import scala.collection.JavaConversions._

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/11/12 14:19
 */
object MapState {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val list = List("I love you", "hello spark", "hello flink", "hello hadoop")
    //    val stream = env.fromElements(list)
    val stream = env.fromCollection(list)
    stream.flatMap(new FlatMapFunction[String, String] {
      override def flatMap(t: String, collector: Collector[String]): Unit = {
        val strings = t.split(" ")
        strings.foreach(u => {
          collector.collect(u)
        })
      }
    }).map(new MapFunction[String, (String, Int)] {
      override def map(t: String): (String, Int) = {
        (t, 1)
      }
    }).keyBy(new KeySelector[(String, Int), String] {
      override def getKey(in: (String, Int)): String = {
        in._1
      }
    }).map(new RichMapFunction[(String, Int), (String, Int)] {
      private var map: MapState[String, Int] = _

      override def open(parameters: Configuration): Unit = {
        //定义map state存储的数据类型
        val desc = new MapStateDescriptor[String, Int]("sum", classOf[String], classOf[Int])
        //注册map state
        map = getRuntimeContext.getMapState(desc)
      }

      override def map(value: (String, Int)): (String, Int) = {
        val key = value._1
        val v = value._2
        if (map.contains(key)) {
          map.put(key, map.get(key) + 1)
        } else {
          map.put(key, 1)
        }
        val iterator = map.keys().iterator()
        while (iterator.hasNext) {
          val key = iterator.next()
          println("word:" + key + "\t count:" + map.get(key))
        }
        value
      }
    }).setParallelism(3)
    env.execute()
  }
}
