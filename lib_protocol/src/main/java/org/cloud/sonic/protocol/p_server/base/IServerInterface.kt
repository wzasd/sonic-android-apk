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

package org.cloud.sonic.protocol.p_server.base

import org.cloud.sonic.protocol.bean.DataMsg
import org.cloud.sonic.protocol.p_server.config.ServerOption
import org.cloud.sonic.protocol.p_server.listener.IMsgReceivedListener

/**
 *  @author : jeffery
 *  @date : 2022/9/8 21:49
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
interface IServerInterface {
    /**
     * 初始化
     *
     * @param options
     * @return
     */
    fun init(options: ServerOption, msgReceivedListener: IMsgReceivedListener?): Boolean

    /**
     * 启动IMS
     */
    fun start()

    /**
     * 发送消息
     *
     * @param msg
     */
    fun sendMsg(msg: DataMsg)

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param isJoinResendManager 是否加入消息重发管理器
     */
    fun sendMsg(msg: DataMsg, isJoinResendManager: Boolean)

    /**
     * 释放资源
     */
    fun release()
}