package com.csy.flink.scala.training

import com.alibaba.fastjson.annotation.JSONField

/**
 * <p>Description：</p>
 *
 * @author shuyun.cheng
 * @date 2020/10/27 17:30
 */
case class RequestLogMsg(
                     @JSONField(name="account")
                     account: String,
                     @JSONField(name="request_json")
                     requestJson: String,
                     @JSONField(name="request_ip")
                     requestIp: String,
                     @JSONField(name="request_time")
                     requestTime: Long,
                     @JSONField(name="msg_type")
                     msgType: String,
                     status: Int,
                     @JSONField(name="swift_number")
                     swiftNumber: String,
                     @JSONField(name="response_time")
                     responseTime: Long,
                     @JSONField(name="response_json")
                     responseJson: String,
                     @JSONField(name="u_time")
                     uTime: Int,
                     @JSONField(name="response_code")
                     responseCode: String
                   )

case class ResponseJson(
                           @JSONField(name="swift_number")
                           swiftNumber: String,
                           code: String
                         )

case class RequestJson(
                          @JSONField(name="account")
                          account: String,
                          @JSONField(name="is_sha256")
                          idSha256: String,
                          @JSONField(name="cell_sha256")
                          cellSha256: Array[String]
                        )