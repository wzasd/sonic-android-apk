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

package org.cloud.sonic.protocol.p_client

import org.cloud.sonic.protocol.config.CommunicationProtocol
import org.cloud.sonic.protocol.config.ImplementationMode
import org.cloud.sonic.protocol.config.TransportProtocol
import org.cloud.sonic.protocol.p_client.base.IClientInterface
import org.cloud.sonic.protocol.p_client.config.ClientOption
import org.cloud.sonic.protocol.p_client.listener.IConnectStatusListener
import org.cloud.sonic.protocol.p_server.listener.IMsgReceivedListener
import org.cloud.sonic.protocol.util.SonicPLog

/**
 *  @author : jeffery
 *  @date : 2022/10/12 20:42
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
object SonicClientKit {
    private const val TAG = "SonicSocketKit"
    var client : IClientInterface? = null

    fun init(option: ClientOption?, connectStatusListener: IConnectStatusListener?,
             msgReceivedListener: IMsgReceivedListener?): Boolean {
        if (option == null) {
            SonicPLog.e(TAG, "SonicSocketKit initialization failed：ClientOption 为 null")
            return false
        }

        val implementationMode: ImplementationMode? = option.implementationMode
        if (implementationMode == null) {
            SonicPLog.e(TAG, "SonicSocketKit initialization failed：ImplementationMode 为 null")
            return false
        }

        val communicationProtocol: CommunicationProtocol? = option.communicationProtocol
        if (communicationProtocol == null) {
            SonicPLog.e(TAG, "SonicSocketKit initialization failed：CommunicationProtocol 为 null")
            return false
        }

        val transportProtocol: TransportProtocol? = option.transportProtocol
        if (transportProtocol == null) {
            SonicPLog.e(TAG, "SonicSocketKit initialization failed：TransportProtocol 为 null")
            return false
        }

        client = SonicClientFactory.getSocketClient(implementationMode, communicationProtocol)
        if (client == null) {
            SonicPLog.e(TAG, "SonicSocketKit initialization failed：ims 为 null")
            return false
        }

        val initialized: Boolean = client!!.init(option, connectStatusListener, msgReceivedListener)
        if (!initialized) {
            SonicPLog.e(TAG, "SonicSocketKit initialization failed：please check ${client!!.javaClass.simpleName.toString()} related logs")
            return false
        }

        SonicPLog.d(TAG, "SonicSocketKit loading finished ims = ${client!!.javaClass.simpleName.toString()} options = $option".trimIndent())
        return true
    }

    fun connect() {
        if (client == null) {
            SonicPLog.d(TAG, "SonicSocketKit failed to activate")
            return
        }
        client?.connect()
    }

    fun disconnect() {
        client?.release()
    }
}