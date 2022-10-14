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

import android.content.Context
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
     * initialization
     *
     * @param context
     * @param options               Initial configuration
     * @param connectStatusListener connection status monitor
     * @param msgReceivedListener   message reception monitor
     */
    fun init(
        options: ClientOption,
        connectStatusListener: IConnectStatusListener?,
        msgReceivedListener: IMsgReceivedListener?
    ): Boolean

    /**
     * connect
     */
    fun connect()

    /**
     * reconnect
     *
     * @param isFirstConnect whether to connect for the first time
     */
    fun reconnect(isFirstConnect: Boolean)

    /**
     * Send a message
     *
     * @param msg
     */
    fun sendMsg(msg: MessageProtobuf.Msg)

    /**
     * Send a message
     * overload
     *
     * @param msg
     * @param listener message sending status listener
     */
    fun sendMsg(msg: MessageProtobuf.Msg, listener: IMsgSentStatusListener?)

    /**
     * Send a message
     * overload
     *
     * @param msg
     * @param isJoinResendManager Whether to join the message resend manager
     */
    fun sendMsg(msg: MessageProtobuf.Msg, isJoinResendManager: Boolean)

    /**
     * Send a message
     * overload
     *
     * @param msg
     * @param listener            message sending status listener
     * @param isJoinResendManager Whether to join the message resend manager
     */
    fun sendMsg(msg: MessageProtobuf.Msg, listener: IMsgSentStatusListener?, isJoinResendManager: Boolean)

    /**
     * release
     */
    fun release()
}