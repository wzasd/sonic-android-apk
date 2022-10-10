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

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import org.cloud.sonic.protocol.bean.MessageProtobuf

/**
 *  @author : jeffery
 *  @date : 2022/9/8 22:12
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
class NettyWebSocketClientChannelInitializerHandler(private val client: NettyWebSocketClient): ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel?) {
        val pipeline: ChannelPipeline? = ch?.pipeline()
        pipeline?.let {
            // A custom length decoder provided by netty to solve the problem of TCP unpacking and sticking
            pipeline.addLast("frameEncoder", LengthFieldPrepender(2))
            pipeline.addLast("frameDecoder", LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))

            // Add protobuf codec support
            pipeline.addLast(ProtobufEncoder())
            pipeline.addLast(ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()))
        }

        ch?.pipeline()?.addLast(NettyWebSocketClientReadHandler(client))
    }
}