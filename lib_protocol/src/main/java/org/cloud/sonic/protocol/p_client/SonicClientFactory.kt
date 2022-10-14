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
import org.cloud.sonic.protocol.config.CommunicationProtocol.WebSocket
import org.cloud.sonic.protocol.config.ImplementationMode
import org.cloud.sonic.protocol.config.ImplementationMode.Netty
import org.cloud.sonic.protocol.p_client.base.IClientInterface
import org.cloud.sonic.protocol.p_client.netty.websocket.NettyWebSocketClient

/**
 *  @author : jeffery
 *  @date : 2022/10/12 20:42
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
object SonicClientFactory {

    fun getSocketClient(
        implementationMode: ImplementationMode?,
        communicationProtocol: CommunicationProtocol?
    ): IClientInterface? {
        when (implementationMode) {
            Netty -> when (communicationProtocol) {
                WebSocket -> return NettyWebSocketClient
                else -> {}
            }
            else -> {

            }
        }
        return null
    }
}