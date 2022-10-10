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

package org.cloud.sonic.protocol.p_server.manager

import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

/**
 *  @author : jeffery
 *  @date : 2022/9/14 22:31
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
object ChannelContainer {
    private val CHANNELS: MutableMap<String, NettyChannel> =    ConcurrentHashMap<String, NettyChannel>()

    fun saveChannel(channel: NettyChannel?) {
        channel?.let {
            CHANNELS[it.getChannelId()] = it
        }
    }

    fun removeChannelIfConnectNoActive(channel: Channel?): NettyChannel? {
        if (channel == null) {
            return null
        }
        val channelId = channel.id().toString()
        return removeChannelIfConnectNoActive(channelId)
    }

    fun removeChannelIfConnectNoActive(channelId: String): NettyChannel? {
        return if (CHANNELS.containsKey(channelId) && !CHANNELS[channelId]!!.isActive()) {
            CHANNELS.remove(channelId)
        } else null
    }

    fun getUserIdByChannel(channel: Channel): String? {
        return getUserIdByChannel(channel.id().toString())
    }

    fun getUserIdByChannel(channelId: String): String? {
        return if (CHANNELS.containsKey(channelId)) {
            CHANNELS[channelId]?.userId
        } else null
    }

    fun getActiveChannelByUserId(userId: String): NettyChannel? {
        for ((_, value) in CHANNELS) {
            if (value.userId == userId && value.isActive()) {
                return value
            }
        }
        return null
    }

}