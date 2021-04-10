package com.csy.flink.scala.training

import com.alibaba.fastjson.JSON
import com.typesafe.scalalogging.LazyLogging
;

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/10/27 17:50
 */
object ParseHxLog extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val hxLogStr: String = "{\"request_json\":\"{\\\"account\\\":\\\"000000\\\",\\\"id_sha256\\\":\\\"ef7c807134eddeb17422f6125da5eba3777868686787689yuiohasdkjhfkasjd\\\",\\\"cell_sha256\\\":[\\\"5f3c80d1eebd515f3e748df58e8564d81543d1e5574e2839d5f0d8c19fe699bd\\\",\\\"5f3c80d1eeb\\\"]}\",\"request_ip\":\"ip1,ip2\",\"request_time\":1565266221000,\"msg_type\":\"request\",\"status\":1,\"swift_number\":\"111125_20190807134950_2787\",\"account\":\"000000\",\"response_time\":1565156990608,\"response_json\":\"{\\\"swift_number\\\":\\\"111125_20190807134950_2787\\\",\\\"code\\\":\\\"100002\\\"}\",\"u_time\":74,\"response_code\":\"100002\"}"
    val hxLogObj = JSON.parseObject(hxLogStr, classOf[RequestLogMsg])
    println(hxLogObj.account)
    val requestJson: String = hxLogObj.requestJson
    val value: RequestJson = JSON.parseObject(requestJson, classOf[RequestJson])
    println(value.account)
    val sha25 = value.cellSha256
    val str = sha25.apply(1)
    println(str)
    for (a <- sha25) {
      println(a)
    }
    println(value.cellSha256(0))
  }
}
