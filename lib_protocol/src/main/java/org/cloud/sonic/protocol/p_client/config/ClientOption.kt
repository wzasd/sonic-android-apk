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

package org.cloud.sonic.protocol.p_client.config

import org.cloud.sonic.protocol.config.CommunicationProtocol
import org.cloud.sonic.protocol.config.ImplementationMode
import org.cloud.sonic.protocol.config.TransportProtocol

/**
 *  @author : jeffery
 *  @date : 2022/9/8 23:33
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
class ClientOption {
     var implementationMode // 实现方式
            : ImplementationMode? = null
     var communicationProtocol // 通信协议
            : CommunicationProtocol? = null
     var transportProtocol // 传输协议
            : TransportProtocol? = null
     var connectTimeout // 连接超时时间，单位：毫秒
            = 0
     var reconnectInterval // 重连间隔时间，单位：毫秒
            = 0L
     var reconnectCount // 单个地址一个周期最大重连次数
            = 0
     var foregroundHeartbeatInterval // 应用在前台时心跳间隔时间，单位：毫秒
            = 0L
     var backgroundHeartbeatInterval // 应用在后台时心跳间隔时间，单位：毫秒
            = 0L
     var autoResend // 是否自动重发消息
            = false
     var resendInterval // 自动重发间隔时间，单位：毫秒
            = 0
     var resendCount // 消息最大重发次数
            = 0
     var serverList // 服务器地址列表
            : List<String>? = null
    
    inline fun build(func: ClientOption.() -> Unit) {
        this.func()
    }
}