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

/**
 *  @author : jeffery
 *  @date : 2022/9/14 22:32
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
data class NettyChannel(
    var userId: String,
    var channel: Channel
) {
    fun getChannelId(): String {
        return channel.id().toString()
    }

    fun isActive(): Boolean {
        return channel.isActive
    }
}