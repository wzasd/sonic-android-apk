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

package org.cloud.sonic.protocol.p_client.netty.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.cloud.sonic.protocol.p_client.config.IConnectStatus
import org.cloud.sonic.protocol.util.SonicPLog

/**
 *  @author : jeffery
 *  @date : 2022/9/8 22:12
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
class NettyWebSocketClientReadHandler(private val client: NettyWebSocketClient): ChannelInboundHandlerAdapter() {
    private val TAG: String = "NettyWebSocketClientReadHandler"

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        SonicPLog.d(TAG, "channelActive() ctx = $ctx")
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        super.channelRead(ctx, msg)
    }

    /**
     * 关闭channel并重连
     * @param ctx
     */
    private fun closeChannelAndReconnect(ctx: ChannelHandlerContext) {
        SonicPLog.d(TAG, "准备关闭channel并重连")
        val channel = ctx.channel()
        channel?.close()
        // 回调连接状态
        client.callbackIMSConnectStatus(IConnectStatus.CONNECT_FAILED)
        // 触发重连
        client.reconnect(false)
    }
}