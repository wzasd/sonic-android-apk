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

package org.cloud.sonic.protocol.p_client.config

/**
 *  @author : jeffery
 *  @date : 2022/9/8 23:10
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
enum class IConnectStatus(var errCode: Int, var errMsg: String) {
    UNCONNECTED(0, "未连接"),
    CONNECTING(1, "连接中"),
    CONNECTED(2, "连接成功"),
    CONNECT_FAILED(-100, "连接失败"),
    CONNECT_FAILED_CLOSED(-101, "连接失败：服务器已关闭"),
    CONNECT_FAILED_SERVER_LIST_EMPTY(-102, "连接失败：服务器地址列表为空"),
    CONNECT_FAILED_SERVER_EMPTY(-103, "连接失败：服务器地址为空"),
    CONNECT_FAILED_SERVER_ILLEGITIMATE(-104, "连接失败：服务器地址不合法"),
}