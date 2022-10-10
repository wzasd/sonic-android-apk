/*
 *
 * Copyright (C) [SonicCloudOrg] Sonic Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.cloud.sonic.protocol.bean

/**
 *  @author : jeffery
 *  @date : 2022/9/8 21:49
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :通用的消息格式定义，可转换成 json 或 protobuf 传输
 */
class DataMsg {
    private val msgId : String? = null // 消息唯一标识
    private val msgType  = 0// 消息类型
    private val sender : String? = null // 发送者标识
    private val receiver : String? = null // 接收者标识
    private val timestamp : Long = 0 // 消息发送时间，单位：毫秒
    private val report = 0// 消息发送状态报告
    private val content : String? = null// 消息内容
    private val contentType = 0 // 消息内容类型
    private val data : String? = null// 扩展字段，以key/value形式存储的json字符串

    inline fun build(func:DataMsg.() -> Unit){
        this.func()
    }
}