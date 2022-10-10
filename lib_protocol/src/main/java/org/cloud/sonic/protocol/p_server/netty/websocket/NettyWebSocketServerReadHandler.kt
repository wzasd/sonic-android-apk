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

package org.cloud.sonic.protocol.p_server.netty.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.cloud.sonic.protocol.bean.MessageProtobuf
import org.cloud.sonic.protocol.p_server.manager.ChannelContainer
import org.cloud.sonic.protocol.p_server.manager.NettyChannel

/**
 *  @author : jeffery
 *  @date : 2022/9/8 22:12
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
class NettyWebSocketServerReadHandler(val server: NettyWebSocketServer): ChannelInboundHandlerAdapter() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        println("ServerHandler channelActive() ${ctx.channel().remoteAddress()}")
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        println("ServerHandler channelInactive()")
        // After the user disconnects, remove the channel
        ChannelContainer.removeChannelIfConnectNoActive(ctx!!.channel())
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        val message: MessageProtobuf.Msg = msg as MessageProtobuf.Msg
        println("received message from client：$message")
        when (message.head.msgType) {
            1001 -> handshakeMessage(message,ctx)
            1002 -> heartbeatMessage(message)
            2001 -> clientMessaging(message)
            3001 -> {}
            else -> {}
        }
    }

    private fun handshakeMessage(message: MessageProtobuf.Msg, ctx: ChannelHandlerContext) {
        val fromId = message.head.fromId
        // After the handshake is successful, save the user channel
        ChannelContainer.saveChannel(NettyChannel(fromId, ctx.channel()))
        ChannelContainer.getActiveChannelByUserId(fromId)?.channel?.writeAndFlush(message)
    }
    private fun heartbeatMessage(message: MessageProtobuf.Msg) {
        // Receive a heartbeat message and return it as it is
        val fromId = message.head.fromId
        ChannelContainer.getActiveChannelByUserId(fromId)?.channel?.writeAndFlush(message)
    }
    private fun clientMessaging(message: MessageProtobuf.Msg) {
        // Receive a 2001 or 3001 message and return the message to the client to send a status report
        val fromId = message.head.fromId
        val sentReportMsgBuilder = MessageProtobuf.Msg.newBuilder()
        val sentReportHeadBuilder = MessageProtobuf.Head.newBuilder()
        sentReportHeadBuilder.msgId = message.head.msgId
        sentReportHeadBuilder.msgType = 1010
        sentReportHeadBuilder.timestamp = System.currentTimeMillis()
        sentReportHeadBuilder.statusReport = 1
        sentReportMsgBuilder.head = sentReportHeadBuilder.build()
        ChannelContainer.getActiveChannelByUserId(fromId)?.channel?.writeAndFlush(sentReportMsgBuilder.build())

        // Simultaneously forward the message to the recipient
        val toId = message.head.toId
        ChannelContainer.getActiveChannelByUserId(toId)?.channel?.writeAndFlush(message)
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        super.userEventTriggered(ctx, evt)
        println("ServerHandler userEventTriggered()")
    }
}