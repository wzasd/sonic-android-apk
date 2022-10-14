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

package org.cloud.sonic.android.utils

import android.util.Log
import org.cloud.sonic.protocol.p_client.SonicClientKit
import org.cloud.sonic.protocol.config.CommunicationProtocol
import org.cloud.sonic.protocol.config.ImplementationMode
import org.cloud.sonic.protocol.config.TransportProtocol
import org.cloud.sonic.protocol.p_client.config.ClientOption
import org.cloud.sonic.protocol.p_client.listener.IConnectStatusListener
import org.cloud.sonic.protocol.p_server.SonicServiceKit
import org.cloud.sonic.protocol.p_server.config.ServerOption

/**
 *  @author : jeffery
 *  @date : 2022/10/12 21:18
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
object SocketServerManager {
    private const val TAG = "SocketManager"

    fun createServer(): Boolean {
        val options: ServerOption = ServerOption().build {
            implementationMode = ImplementationMode.Netty
            communicationProtocol = CommunicationProtocol.WebSocket
            transportProtocol = TransportProtocol.Protobuf
        }
        val initSucceed: Boolean = SonicServiceKit.init(options, null)
        if (initSucceed) SonicServiceKit.start()
        return initSucceed
    }


}