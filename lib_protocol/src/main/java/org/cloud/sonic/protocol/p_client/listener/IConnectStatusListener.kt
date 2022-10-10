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

package org.cloud.sonic.protocol.p_client.listener

/**
 *  @author : jeffery
 *  @date : 2022/9/8 22:56
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description : connection status listener
 */
interface IConnectStatusListener {
    fun onUnconnected()
    fun onConnecting()
    fun onConnected()
    fun onConnectFailed(errCode: Int, errMsg: String?)
}