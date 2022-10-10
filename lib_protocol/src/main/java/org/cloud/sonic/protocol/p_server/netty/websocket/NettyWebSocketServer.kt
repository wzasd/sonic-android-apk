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

import android.annotation.SuppressLint
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.cloud.sonic.protocol.bean.DataMsg
import org.cloud.sonic.protocol.p_server.config.ServerOption
import org.cloud.sonic.protocol.p_server.base.IServerInterface
import org.cloud.sonic.protocol.p_server.listener.IMsgReceivedListener
import org.cloud.sonic.protocol.util.SonicPLog
import java.net.Inet4Address


object NettyWebSocketServer: IServerInterface {
    private const val TAG = "NettyWebSocketServer"
    @Volatile var isClosed: Boolean = false
    private var initialized = false

    private var channel: Channel? = null
    private var bootstrap: ServerBootstrap? = null
    private var bossGroup: EventLoopGroup? = null
    private var workGroup: EventLoopGroup? = null

    private var mMsgReceivedListener: IMsgReceivedListener? = null

    private lateinit var mServerOptions: ServerOption

    override fun init(options: ServerOption, msgReceivedListener: IMsgReceivedListener?): Boolean {
        this.mServerOptions = options
        this.mMsgReceivedListener = msgReceivedListener
        initialized = true
        return true
    }

    @SuppressLint("DefaultLocale")
    override fun start() {
        if (!initialized) {
            SonicPLog.e(TAG,"NettyWebSocket start fail ：please init first");
            return
        }
        try {
            initServerBootstrap()
            val future: ChannelFuture? = bootstrap?.bind(mServerOptions.port)?.sync()
            channel = future?.channel()
            channel?.let {
                if (it.isOpen && it.isActive && it.isRegistered && it.isWritable) {
                    SonicPLog.d(TAG, String.format(
                        "NettyWebSocket 启动成功，address = ws://%1\$s:%2\$d/websocket",
                        Inet4Address.getLocalHost().hostAddress,
                        mServerOptions.port
                    )
                    )
                    future?.awaitUninterruptibly()
                    channel?.closeFuture()?.sync()
                } else {
                    SonicPLog.d(TAG,"NettyWebSocket 启动失败")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bossGroup?.shutdownGracefully()
                workGroup?.shutdownGracefully()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                bossGroup = null
                workGroup = null
            }
        }
    }

    override fun sendMsg(msg: DataMsg) {
    }

    override fun sendMsg(msg: DataMsg, isJoinResendManager: Boolean) {
    }

    override fun release() {
        closeChannel();
        closeServerBootstrap();
        isClosed = true;
    }

    /**
     * 初始化ServerBootstrap
     */
    private fun initServerBootstrap() {
        try {
            closeServerBootstrap() // 先关闭之前的bootstrap
            // boss线程池用于处理TCP连接，通常服务端开启的都是一个端口，所以线程数指定为1即可
            bossGroup = NioEventLoopGroup(1)
            // work线程用于处理IO事件，需要多线程处理，不指定线程数，默认就是CPU核心数*2
            workGroup = NioEventLoopGroup()
            bootstrap = ServerBootstrap()
            bootstrap!!.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel::class.java) // 设置TCP接收缓冲区大小（字节数）
                .option(
                    ChannelOption.SO_RCVBUF,
                    32 * 1024
                ) // 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值，Windows为200，其他为128
                .option(ChannelOption.SO_BACKLOG, 256) // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
                .childOption(
                    ChannelOption.SO_KEEPALIVE,
                    true
                ) // 设置禁用nagle算法，如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(NettyWebSocketServerChannelInitializerHandler(this))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun closeServerBootstrap() {
        try {
            bootstrap?.config()?.group()?.shutdownGracefully()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bootstrap = null
        }
    }

    private fun closeChannel() {
        try {
            channel?.let {
                it.close()
                it.eventLoop().shutdownGracefully()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            channel = null
        }
    }
}