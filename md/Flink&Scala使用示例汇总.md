## 一、本地启动flink web

##### 1、导入jar包
```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-runtime-web_2.11</artifactId>
    <version>${flink.version}</version>
</dependency>
```

##### 2、配置代码
```java
val conf: Configuration = new Configuration()
import org.apache.flink.configuration.ConfigConstants
conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true)
//自定义端口
conf.setInteger(RestOptions.PORT, 8050)
//本地env
val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
```

##### 3、访问地址

```http
http://localhost:8050
```

## 二、scala循环中断

```scala
object a {
  def main(args: Array[String]): Unit = {
  // 定义一个数组
    val arr = Array(1,4,2,7,9,10)
    val bs = new Breaks
  // 在循环外面是break 
    bs.breakable(
      arr.foreach(i=>{
        if (i == 7){
          bs.break()
        }
        print(i+"  ")
      })
    )
  println()
  
    arr.foreach(i=> {
  // 在循环里面是continue
      bs.breakable {
        if (i == 7) {
          bs.break()
        }
        print(i + "  ")
    }
    })
  }
}
```

## 三、Flink分区策略

1. shuffle 

   - 场景：增大分区、提高并行度，解决数据倾斜 
   - DataStream → DataStream 
   - 分区元素随机均匀分发到下游分区，网络开销比较大

2. rebalance 

   - 场景：增大分区、提高并行度，解决数据倾斜 
   - DataStream → DataStream 
   - 轮询分区元素，均匀的将元素分发到下游分区，下游每个分区的数据比较均匀，在发生数据倾斜时非常 有用，网络开销比较大

3. rescale 

   - 场景：减少分区 防止发生大量的网络传输 不会发生全量的重分区
   -  DataStream → DataStream 
   - 通过轮询分区元素，将一个元素集合从上游分区发送给下游分区，发送单位是集合，而不是一个个元素 
   - 注意：rescale发生的是本地数据传输，而不需要通过网络传输数据，比如taskmanager的槽数。简单 来说，上游的数据只会发送给本TaskManager中的下游

4. broadcast 

   - 场景：需要使用映射表、并且映射表会经常发生变动的场景 
   - DataStream → DataStream 
   - 上游中每一个元素内容广播到下游每一个分区中

5. global 

   - 场景：并行度降为1 
   - DataStream → DataStream 
   - 上游分区的数据只分发给下游的第一个分区

6. forward

   - 场景：一对一的数据分发，map、flatMap、filter 等都是这种分区策略 
   - DataStream → DataStream 
   - 上游分区数据分发到下游对应分区中 partition1->partition1 partition2->partition2
   - 注意：必须保证上下游分区数（并行度）一致，不然会有如下异常：Forward partitioning does not allow change of parallelism

7. keyBy 

   - 场景：与业务场景匹配 
   - DataStream → DataStream 
   - 根据上游分区元素的Hash值与下游分区数取模计算出，将当前元素分发到下游哪一个分区

8. PartitionCustom 

   - 自定义分区策略
   - DataStream → DataStream 
   - 通过自定义的分区器，来决定元素是如何从上游分区分发到下游分区

## 四、Flink的状态

#### 1、概述

​		**Flink**是一个有状态的流式计算引擎，在流处理的过程中，要对数据的中间（结果）状态进行保存，默认保存在**TaskManager**的堆内存中，但是当**task**挂掉后，状态就会随之丢失，造成结果的不准确，那么如果想保证结果的准确性，就需要将流重新计算一遍，效率极低。想要保证At -leastonce和Exactly-once，需要把数据状态持久化到更安全的存储介质中，Flink提供了堆内内存、堆外内存、HDFS、RocksDB等存储介质，另外也可以存储在共享缓存种。

------

#### 2、Flink中状态分为两种类型

**Keyed State** 

- 基于KeyedStream上的状态，这个状态是跟特定的Key绑定，KeyedStream流上的每一个Key都对 应一个State，每一个Operator可以启动多个Thread处理，但是相同Key的数据只能由同一个 Thread处理，因此一个Keyed状态只能存在于某一个Thread中，一个Thread会有多个Keyed state。

**Non-Keyed State（Operator State）** 

- Operator State与Key无关，而是与Operator绑定，整个Operator只对应一个State。比如：Flink 中的Kafka Connector就使用了Operator State，它会在每个Connector实例中，保存该实例消费 Topic的所有(partition, offset)映射

#### 3、Flink针对Keyed State提供了以下可以保存State的数据结构

- **ValueState：**类型为T的单值状态，这个状态与对应的Key绑定，最简单的状态，通过update更新值，通过value获取状态值 。
- **ListState：**Key上的状态值为一个列表，这个列表可以通过add方法往列表中添加值，也可以通过 get()方法返回一个Iterable来遍历状态值。
- **ReducingState：**每次调用add()方法添加值的时候，会调用用户传入的reduceFunction，最后合 并到一个单一的状态值。
- **MapState：**状态值为一个Map，用户通过put或putAll方法添加元素，get(key)通过指定的 key获取value，使用entries()、keys()、values()检索。
- **AggregatingState：**保留一个单值，表示添加到状态的所有值的聚合。和 ReducingState 相反的是, 聚合类型可能与添加到状态的元素的类型不同。使用 add(IN) 添加 的元素会调用用户指定的 AggregateFunction 进行聚合。
- **FoldingState：**已过时建议使用AggregatingState 保留一个单值，表示添加到状态的所有 值的聚合。 与 ReducingState 相反，聚合类型可能与添加到状态的元素类型不同。 使用 add（T） 添加的元素会调用用户指定的 FoldFunction 折叠成聚合值。

## 五、java和scala的隐式转换

在使用scala编写flink流处理逻辑过程中，经常会使用到java类型，table api，但是直接导入包的话都是scala类型的，编译会报错，无法自动隐式转换，这个时候，可以引入隐式转换的包：

```scala
//java-scala
import scala.collection.JavaConversions._
//stream-table-scala
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
```

## 六、Env的一些初始化设置

#### 1、checkpoint和状态存储相关配置

```scala
import java.util.concurrent.TimeUnit

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.{CheckpointConfig, StreamExecutionEnvironment}

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/11/12 16:54
 */
object CheckoutPointConfig {
  def main(args: Array[String]): Unit = {
    val pulsar_topic: String = "real-time-billing"
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //checkpoint配置
    env.enableCheckpointing(60000 * 5) //默认情况checkpoint 是禁用的
    //checkpoint 超时：如果 checkpoint 执行的时间超过了该配置的阈值，还在进行中的 checkpoint 操作就会被抛弃
    env.getCheckpointConfig.setCheckpointTimeout(60000 * 30)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    //checkpoints 之间的最小时间, 无论 checkpoint 持续时间与间隔是多久，在前一个 checkpoint 完成时的至少五秒后会才开始下一个 checkpoint。
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(30000)
    //最大失败次数
    env.getCheckpointConfig.setTolerableCheckpointFailureNumber(10)
    //同一时间只允许一个 checkpoint 进行
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    //job取消时是否保留该checkpoint
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    //允许job从当前最近一个最近checkpoint恢复
    env.getCheckpointConfig.setPreferCheckpointForRecovery(true)
    //    设置statebackend
    env.setStateBackend(new RocksDBStateBackend(s"hdfs:///tmp/flink/ck/${pulsar_topic}", true))
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, Time.of(10, TimeUnit.SECONDS)))
    env.setParallelism(3)
  }
}
```

#### 2、时间语义的定义

Flink定义了三类时间

- 处理时间（Process Time）数据进入Flink被处理的系统时间（Operator处理数据的系统时间）
- 事件时间（Event Time）数据在数据源产生的时间，一般由事件中的时间戳描述，比如用户日志 中的TimeStamp
- 摄取时间（Ingestion Time）数据进入Flink的时间，记录被Source节点观察到的系统时间 

![](C:\Users\shuyun.cheng\Desktop\flink的时间语义.png)

- Flink流式计算的时候需要显示定义时间语义，根据不同的时间语义来处理数据，比如指定的时间语义是 事件时间，那么我们就要切换到事件时间的世界观中，窗口的起始与终止时间都是以事件时间为依据
- 在Flink中默认使用的是Process Time，如果要使用其他的时间语义，在执行环境中可以设置

```scala
//设置时间语义为Ingestion Time
env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
//设置时间语义为Event Time 我们还需要指定一下数据中哪个字段是事件时间（下文会讲）
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
```