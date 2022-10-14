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

import android.annotation.SuppressLint
import io.netty.channel.Channel
import io.netty.util.internal.StringUtil
import org.cloud.sonic.protocol.p_client.config.ClientOption
import org.cloud.sonic.protocol.p_client.config.IConnectStatus
import org.cloud.sonic.protocol.util.SonicPLog
import java.net.URI

/**
 *  @author : jeffery
 *  @date : 2022/9/8 23:48
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
class NettyWebSocketReconnectTask(private val client: NettyWebSocketClient ): Runnable {
    private val TAG = "NettyWebSocketReconnectTask"

    var mOption: ClientOption = client.options
    
    @SuppressLint("DefaultLocale")
    override fun run() {
        try {
            // 重连时，释放工作线程组，也就是停止心跳
            client.executors?.destroyWorkLoopGroup()

            // client 未关闭
            while (!client.isClosed) {
                var status: IConnectStatus
                if (connect().also { status = it } === IConnectStatus.CONNECTED) {
                    client.callbackIMSConnectStatus(status)
                    break // 连接成功，跳出循环
                }
                if (status === IConnectStatus.CONNECT_FAILED || status === IConnectStatus.CONNECT_FAILED_CLOSED || status === IConnectStatus.CONNECT_FAILED_SERVER_LIST_EMPTY || status === IConnectStatus.CONNECT_FAILED_SERVER_EMPTY || status === IConnectStatus.CONNECT_FAILED_SERVER_ILLEGITIMATE) {
                    client.callbackIMSConnectStatus(status)
                    if (client.isClosed) {
                        return
                    }
                    // 一个服务器地址列表都连接失败后，说明网络情况可能很差，延时指定时间（重连间隔时间*2）再去进行下一个服务器地址的连接
                    SonicPLog.d(TAG, String.format("一个周期连接失败，等待%1\$dms后再次尝试重连", mOption!!.reconnectInterval * 2))
                    try {
                        Thread.sleep(mOption!!.reconnectInterval * 2)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        } finally {
            // 标识重连任务停止
            client.setReconnecting(false)
        }
    }

    /**
     * 连接服务器
     * @return
     */
    private fun connect(): IConnectStatus {
        if (client.isClosed) return IConnectStatus.CONNECT_FAILED_CLOSED
        val serverList: List<String>? = mOption.serverList
        if (serverList == null || serverList.isEmpty()) {
            return IConnectStatus.CONNECT_FAILED_SERVER_LIST_EMPTY
        }
        client.initBootstrap()
        for (i in serverList.indices) {
            val server = serverList[i]
            if (StringUtil.isNullOrEmpty(server)) {
                return IConnectStatus.CONNECT_FAILED_SERVER_EMPTY
            }
            var uri: URI
            uri = try {
                URI.create(server)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                return if (i == serverList.size - 1) {
                    SonicPLog.e(TAG, String.format("【%1\$s】连接失败，地址不合法", server))
                    IConnectStatus.CONNECT_FAILED_SERVER_ILLEGITIMATE
                } else {
                    SonicPLog.e(TAG, String.format("【%1\$s】连接失败，地址不合法，正在等待重连，当前重连延时时长：%2\$d", server, mOption.reconnectInterval))
                    SonicPLog.e(TAG, "=========================================================================================")
                    try {
                        Thread.sleep(mOption.reconnectInterval)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                    continue
                }
            }
            if ("ws" != uri.scheme) {
                return if (i == serverList.size - 1) {
                    SonicPLog.e(TAG, String.format("【%1\$s】连接失败，地址不合法", server))
                    IConnectStatus.CONNECT_FAILED_SERVER_ILLEGITIMATE
                } else {
                    SonicPLog.e(TAG, String.format("【%1\$s】连接失败，地址不合法，正在等待重连，当前重连延时时长：%2\$d", server, mOption.reconnectInterval))
                    SonicPLog.e(TAG,
                        "========================================================================================="
                    )
                    try {
                        Thread.sleep(mOption.reconnectInterval)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                    continue
                }
            }
            if (i == 0) {
                client.callbackIMSConnectStatus(IConnectStatus.CONNECTING)
            }

            // +1是因为首次连接也认为是重连，所以如果重连次数设置为3，则最大连接次数为3+1次
            for (j in 0 until mOption.reconnectCount + 1) {
                if (client.isClosed) {
                    return IConnectStatus.CONNECT_FAILED_CLOSED
                }
                SonicPLog.d(TAG, String.format("正在进行【%1\$s】的第%2\$d次连接", server, j + 1))
                try {
                    val host = uri.host
                    val port = uri.port
                    val channel: Channel? = toServer(host, port)
                    if (channel != null && channel.isOpen && channel.isActive && channel.isRegistered && channel.isWritable) {
                        client.channel = channel
                        return IConnectStatus.CONNECTED
                    } else {
                        if (j == mOption.reconnectCount) {
                            // 如果当前已达到最大重连次数，并且是最后一个服务器地址，则回调连接失败
                            if (i == serverList.size - 1) {
                                SonicPLog.e(TAG, String.format("【%1\$s】连接失败", server))
                                return IConnectStatus.CONNECT_FAILED
                            } else {
                                // 一个服务器地址连接失败后，延时指定时间再去进行下一个服务器地址的连接
                                SonicPLog.e(TAG,
                                    String.format(
                                        "【%1\$s】连接失败，正在等待进行下一个服务器地址的重连，当前重连延时时长：%2\$dms",
                                        server,
                                        mOption.reconnectInterval
                                    )
                                )
                                SonicPLog.e(TAG,
                                    "========================================================================================="
                                )
                                Thread.sleep(mOption.reconnectInterval)
                            }
                        } else {
                            // 连接失败，则线程休眠（重连间隔时长 / 2 * n） ms
                            val delayTime: Long =
                                mOption.reconnectInterval + mOption.reconnectInterval / 2 * j
                            SonicPLog.e(TAG,
                                kotlin.String.format(
                                    "【%1\$s】连接失败，正在等待重连，当前重连延时时长：%2\$dms",
                                    server,
                                    delayTime
                                )
                            )
                            Thread.sleep(delayTime)
                        }
                    }
                } catch (e: InterruptedException) {
                    break // 线程被中断，则强制关闭
                }
            }
        }
        return IConnectStatus.CONNECT_FAILED
    }

    /**
     * 真正连接服务器的地方
     * @param host
     * @param port
     * @return
     */
    private fun toServer(host: String, port: Int): Channel? {
        val channel: Channel? = try {
            client.bootstrap?.connect(host, port)?.sync()?.channel()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return channel
    }
}