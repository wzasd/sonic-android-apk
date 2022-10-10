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

package org.cloud.sonic.protocol.p_server.config

import org.cloud.sonic.protocol.config.CommunicationProtocol
import org.cloud.sonic.protocol.config.ImplementationMode
import org.cloud.sonic.protocol.config.ServerConfig
import org.cloud.sonic.protocol.config.TransportProtocol


class ServerOption {
    var implementationMode : ImplementationMode = ImplementationMode.Netty // Implementation Mode
    var communicationProtocol : CommunicationProtocol =
        CommunicationProtocol.WebSocket // communication protocol
    var transportProtocol : TransportProtocol = TransportProtocol.Protobuf // Transport Protocol
    var port = ServerConfig.WEBSOCKET_PORT // port

    fun setCommunicationProtocol(communicationProtocol : CommunicationProtocol): ServerOption {
        this.communicationProtocol = communicationProtocol
        if (port == 0) {
            port = when (communicationProtocol) {
                CommunicationProtocol.TCP -> ServerConfig.TCP_PORT
                CommunicationProtocol.WebSocket -> ServerConfig.WEBSOCKET_PORT
            }
        }
        return this
    }

    fun setImplementationMode(implementationMode: ImplementationMode): ServerOption {
        this.implementationMode = implementationMode
        return this
    }

    fun setTransportProtocol(transportProtocol: TransportProtocol): ServerOption {
        this.transportProtocol = transportProtocol
        return this
    }

    fun setPort(port: Int): ServerOption {
        this.port = port
        return this
    }

    inline fun build(func: ServerOption.() -> Unit) {
        this.func()
    }
}