package com.csy.flink.scala.resource.scala.training.state

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
    // 设置statebackend
    env.setStateBackend(new RocksDBStateBackend(s"hdfs:///tmp/flink/ck/${pulsar_topic}", true))
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, Time.of(10, TimeUnit.SECONDS)))
    env.setParallelism(3)
  }
}
