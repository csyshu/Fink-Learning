package com.csy.flink.scala.resource.scala.training.state

import org.apache.flink.api.common.functions.{MapFunction, RichMapFunction}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/11/12 13:53
 */
object ValueState {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val stream = env.readTextFile("D:\\CodeSpace\\flink_demo\\flink_scala\\src\\main\\java\\com\\csy\\flink_scala\\training\\CarInfo.txt")
    stream.map(new MapFunction[String, CarInfo] {
      override def map(t: String): CarInfo = {
        val carArr = t.split(":")
        CarInfo(carArr(0), carArr(1).toLong)
      }
    }).keyBy(new KeySelector[CarInfo, String] {
      override def getKey(in: CarInfo): String = {
        in.name
      }
    })
      .map(new RichMapFunction[CarInfo, String]() {
        //保存上一次车速
        private var lastTempState: ValueState[Long] = _

        override def open(parameters: Configuration): Unit = {
          val lastTempStateDesc = new ValueStateDescriptor[Long]("lastTempState", classOf[Long])
          lastTempState = getRuntimeContext.getState(lastTempStateDesc)
        }

        override def map(value: CarInfo): String = {
          val lastSpeed = lastTempState.value()
          print("lastSpeed" + lastSpeed)
          this.lastTempState.update(value.speed)
          if ((value.speed - lastSpeed).abs > 15 && lastSpeed != 0)
            "over speed" + value.toString
          else
            value.name
        }
      }).print()
    env.execute()

  }

  case class CarInfo(name: String, speed: Long)

}
