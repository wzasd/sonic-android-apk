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

package org.cloud.sonic.protocol.p_client.base

import org.cloud.sonic.protocol.bean.MessageProtobuf
import org.cloud.sonic.protocol.p_client.config.ClientOption
import org.cloud.sonic.protocol.p_client.listener.IConnectStatusListener
import org.cloud.sonic.protocol.p_client.listener.IMsgSentStatusListener
import org.cloud.sonic.protocol.p_server.listener.IMsgReceivedListener

/**
 *  @author : jeffery
 *  @date : 2022/9/8 23:04
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
interface IClientInterface {
    /**
     * 初始化
     *
     * @param options               IMS初始化配置
     * @param connectStatusListener IMS连接状态监听
     * @param msgReceivedListener   IMS消息接收监听
     */
    fun init(
        options: ClientOption,
        connectStatusListener: IConnectStatusListener?,
        msgReceivedListener: IMsgReceivedListener?
    ): Boolean

    /**
     * 连接
     */
    fun connect()

    /**
     * 重连
     *
     * @param isFirstConnect 是否首次连接
     */
    fun reconnect(isFirstConnect: Boolean)

    /**
     * 发送消息
     *
     * @param msg
     */
    fun sendMsg(msg: MessageProtobuf.Msg)

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param listener 消息发送状态监听器
     */
    fun sendMsg(msg: MessageProtobuf.Msg, listener: IMsgSentStatusListener?)

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param isJoinResendManager 是否加入消息重发管理器
     */
    fun sendMsg(msg: MessageProtobuf.Msg, isJoinResendManager: Boolean)

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param listener            消息发送状态监听器
     * @param isJoinResendManager 是否加入消息重发管理器
     */
    fun sendMsg(msg: MessageProtobuf.Msg, listener: IMsgSentStatusListener?, isJoinResendManager: Boolean)

    /**
     * 释放资源
     */
    fun release()
}