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

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.cloud.sonic.protocol.bean.MessageProtobuf
import org.cloud.sonic.protocol.p_client.base.IClientInterface
import org.cloud.sonic.protocol.p_client.config.ClientOption
import org.cloud.sonic.protocol.p_client.config.IConnectStatus
import org.cloud.sonic.protocol.p_client.config.IConnectStatus.*
import org.cloud.sonic.protocol.p_client.listener.IConnectStatusListener
import org.cloud.sonic.protocol.p_client.listener.IMsgSentStatusListener
import org.cloud.sonic.protocol.p_server.listener.IMsgReceivedListener
import org.cloud.sonic.protocol.util.ExecutorServiceFactory
import org.cloud.sonic.protocol.util.SonicPLog


object NettyWebSocketClient: IClientInterface {
    private val TAG = "NettyWebSocketClient"

    private val lock = Any()

    // client 是否已关闭
    @Volatile var isClosed = true

    // 是否正在进行重连
    @Volatile private var isReconnecting = false

    // 是否已初始化成功
    private var initialized = false

    // client 连接状态监听器
    private var mConnectStatusListener: IConnectStatusListener? = null

    // client 消息接收监听器
    private var mMsgReceivedListener: IMsgReceivedListener? = null

    var bootstrap: Bootstrap? = null
    var channel: Channel? = null

    // client 配置项
    lateinit var options: ClientOption

    // 连接状态
    @Volatile
    private var iConnectStatus: IConnectStatus? = null

    // 线程池组
    var executors: ExecutorServiceFactory? = ExecutorServiceFactory()

    // 是否执行过连接，如果未执行过，在onAvailable()的时候，无需进行重连
    private var isExecConnect = false

    override fun init(
        options: ClientOption,
        connectStatusListener: IConnectStatusListener?,
        msgReceivedListener: IMsgReceivedListener?
    ): Boolean {
        this.options = options
        mConnectStatusListener = connectStatusListener
        mMsgReceivedListener = msgReceivedListener
        executors = ExecutorServiceFactory()
        // 初始化重连线程池
        executors?.initBossLoopGroup()
        // 标识 client 初始化成功
        initialized = true
        // 标识 client 已打开
        isClosed = false
        callbackIMSConnectStatus(UNCONNECTED)
        return true
    }

    override fun connect() {
        if (!initialized) {
            SonicPLog.e(TAG, "Client connect error，please use init() first")
            return
        }
        isExecConnect = true // 标识已执行过连接

        reconnect(true)
    }

    override fun reconnect(isFirstConnect: Boolean) {
        if (!isFirstConnect) {
            // 非首次连接，代表之前已经进行过重连，延时一段时间再去重连
            options?.let {
                try {
                    SonicPLog.e(TAG, String.format("非首次连接，延时%1\$dms再次尝试重连", it.reconnectInterval))
                    Thread.sleep(it.reconnectInterval)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        if (!isClosed && !isReconnecting) {
            synchronized (lock) {
                if (!isClosed && !isReconnecting) {
                    // 标识正在进行重连
                    setReconnecting(true)
                    // 关闭channel
                    closeChannel()
                    // 开启重连任务
                    executors?.execBossTask(NettyWebSocketReconnectTask(this))
                }
            }
        }
    }

    override fun sendMsg(msg: MessageProtobuf.Msg) {
        this.sendMsg(msg, null, true)
    }

    override fun sendMsg(msg: MessageProtobuf.Msg, listener: IMsgSentStatusListener?) {
        this.sendMsg(msg, listener, true)
    }

    override fun sendMsg(msg: MessageProtobuf.Msg, isJoinResendManager: Boolean) {
        this.sendMsg(msg, null, isJoinResendManager)
    }

    override fun sendMsg(
        msg: MessageProtobuf.Msg,
        listener: IMsgSentStatusListener?,
        isJoinResendManager: Boolean
    ) {
        if (!initialized) {
            SonicPLog.e(TAG, "Client 初始化失败，please use init() first")
            return
        }

        if (msg.head == null) {
            println("Failed to send message, message is empty \tmessage=$msg")
            return
        }

        if (channel == null) {
            println("Failed to send message, channel is empty\tmessage=$msg")
        }

        try {
            channel?.writeAndFlush(msg)
        } catch (ex: java.lang.Exception) {
            println("Failed to send message，reason:" + ex.message + "\tmessage=" + msg)
        }
    }

    override fun release() {
        // 关闭channel
        closeChannel()
        // 关闭bootstrap
        closeBootstrap()
        // 标识未进行初始化
        initialized = false
        // 释放线程池组
        executors?.destroy()
        executors = null
    }

    /**
     * 初始化bootstrap
     */
    fun initBootstrap() {
        closeBootstrap() // 初始化前先关闭
        val loopGroup = NioEventLoopGroup(4)
        bootstrap = Bootstrap()
        bootstrap?.group(loopGroup)?.channel(NioSocketChannel::class.java)?.option(
            ChannelOption.SO_KEEPALIVE,
            true
        )?.option(ChannelOption.TCP_NODELAY, true)?.option(ChannelOption.SO_SNDBUF, 32 * 1024)
            ?.option(ChannelOption.SO_RCVBUF, 32 * 1024)?.option(
            ChannelOption.CONNECT_TIMEOUT_MILLIS,
            options?.connectTimeout
        )?.handler(NettyWebSocketClientChannelInitializerHandler(this))

    }

    /**
     * 关闭bootstrap
     */
    private fun closeBootstrap() {
        try {
            bootstrap?.config()?.group()?.shutdownGracefully()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bootstrap = null
        }
    }

    /**
     * 关闭channel
     */
    private fun closeChannel() {
        try {
            channel?.let {
                // 关闭channel时，需要先移除对应handler
                removeHandler(NettyWebSocketClientReadHandler::class.java.simpleName)
                try {
                    it.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    it.eventLoop().shutdownGracefully()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } finally {
            channel = null
        }
    }

    /**
     * 移除handler
     * @param name
     */
    private fun removeHandler(name: String) {
        try {
            val pipeline = channel?.pipeline()
            pipeline?.let {
                if (it[name] != null) {
                    it.remove(name)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            SonicPLog.e(TAG, "移除handler失败：$name")
        }
    }


    /**
     * 回调 Client 连接状态
     *
     * @param connectStatus
     */
    fun callbackIMSConnectStatus(connectStatus: IConnectStatus) {
        SonicPLog.d(TAG, "回调连接状态：$connectStatus")
        if (connectStatus == connectStatus) {
            SonicPLog.d(TAG, "连接状态与上一次相同，无需执行任何操作")
            return
        }
        this.iConnectStatus = connectStatus

        when (connectStatus) {
            UNCONNECTED -> {
                SonicPLog.e(TAG, "Server 未连接")
                mConnectStatusListener?.onUnconnected()
            }
            CONNECTING -> {
                SonicPLog.d(TAG, "Server 连接中")
                mConnectStatusListener?.onConnecting()
            }
            CONNECTED -> {
                SonicPLog.d(TAG, "Server 连接成功")
                mConnectStatusListener?.onConnected()
            }
            CONNECT_FAILED, CONNECT_FAILED_CLOSED, CONNECT_FAILED_SERVER_LIST_EMPTY, CONNECT_FAILED_SERVER_EMPTY, CONNECT_FAILED_SERVER_ILLEGITIMATE -> {
                val errCode: Int = connectStatus.errCode
                val errMsg: String = connectStatus.errMsg
                SonicPLog.e(TAG, "errCode = $errCode\terrMsg = $errMsg")
                mConnectStatusListener?.onConnectFailed(errCode, errMsg)
            }
        }
    }

    /**
     * 标识是否正在进行重连
     * @param isReconnecting
     */
    fun setReconnecting(isReconnecting: Boolean) {
        this.isReconnecting = isReconnecting
    }

}