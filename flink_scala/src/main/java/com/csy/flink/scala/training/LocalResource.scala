package com.csy.flink.scala.training

import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.api.java.io.TextInputFormat
import org.apache.flink.configuration.{Configuration, RestOptions}
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.streaming.api.functions.source.FileProcessingMode
import org.apache.flink.util.Collector
;

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/10/27 17:50
 */
object LocalResource extends LazyLogging {
  def main(args: Array[String]): Unit = {

    val conf: Configuration = new Configuration()
    import org.apache.flink.configuration.ConfigConstants
    conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true)
    //自定义端口
    conf.setInteger(RestOptions.PORT, 8050)
    //本地env
    val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
    //    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val filePath = "D:\\CodeSpace\\flink_demo\\flink_scala\\src\\main\\java\\com\\csy\\flink\\scala\\training\\FileData.txt"
    val filePath1 = "D:\\CodeSpace\\flink_demo\\flink_scala\\src\\main\\java\\com\\csy\\flink\\scala\\training\\FileData1.txt"
    val textInputFormat = new TextInputFormat(new Path(filePath))
    val textInputFormat1 = new TextInputFormat(new Path(filePath1))
    val fileStreamSource = env.readFile(textInputFormat, filePath, FileProcessingMode.PROCESS_CONTINUOUSLY, 1000)
    val fileStreamSource1 = env.readFile(textInputFormat1, filePath1, FileProcessingMode.PROCESS_CONTINUOUSLY, 1000)
    val connectStream = fileStreamSource.connect(fileStreamSource1)
    connectStream.flatMap(new CoFlatMapFunction[String, String, String] {
      var s: String = _

      override def flatMap1(in1: String, collector: Collector[String]): Unit = {
        s = in1
      }

      override def flatMap2(in2: String, collector: Collector[String]): Unit = {
        if (s != null && s.last.equals(in2.charAt(2))) collector.collect(in2 + s) else collector.collect("null")
      }

    }).setParallelism(2).print("out:")
    //        fileStreamSource.flatMap(new FlatMapFunction[String, String] {
    //          override def flatMap(t: String, collector: Collector[String]): Unit = {
    //            val strings = t.split("")
    //            for (s <- strings) {
    //              collector.collect(s)
    //            }
    //          }
    //    }).map(new MapFunction[String, MapData] {
    //      override def map(t: String): MapData = MapData(t, 1)
    //    })
    //      .keyBy(new KeySelector[MapData, String] {
    //        override def getKey(in: MapData): String = {
    //          val num = in.num
    //          num
    //        }
    //      })
    //      .sum(1)
    //      .print()

    //    env.readTextFile(filePath).map(new MapFunction[String, Int] {
    //      override def map(t: String): Int = {
    //        val length = t.length
    //        length
    //      }
    //    }).print()

    //    val value: DataStreamSource[String] = env.readTextFile(filePath)
    //    val map = value.flatMap(new FlatMapFunction[String, String] {
    //      override def flatMap(t: String, collector: Collector[String]): Unit = {
    //        collector.collect(t.split("").toString)
    //      }
    //    })
    //    map.map(new MapFunction[String, String] {
    //      override def map(t: String): String = {
    //        t
    //      }
    //    }).keyBy(0).sum(0).print()


    //循环输出
    //    val fileStream = env.addSource(new SourceFunction[String] {
    //      override def run(sourceContext: SourceFunction.SourceContext[String]): Unit = {
    //        while (true) {
    //          Thread.sleep(1000)
    //          sourceContext.collect("sdfsdsdfsd")
    //        }
    //      }
    //
    //      override def cancel(): Unit = {
    //
    //      }
    //    })
    //    fileStream.map(new RichMapFunction[String, String] {
    //      override def map(in: String): String = {
    //        val str = in
    //        println(str)
    //        str
    //      }
    //    })
    env.execute("file read write")
  }

  case class MapData(num: String, count: Int)

}
