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

package org.cloud.sonic.protocol.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *  @author : jeffery
 *  @date : 2022/9/8 23:14
 *  @email : jayw2016@outlook.com
 *  @github : https://github.com/wzasd
 *  description :
 */
class ExecutorServiceFactory {
    private var bossPool : ExecutorService? = null // 管理线程组，负责重连
    private var workPool : ExecutorService? = null // 工作线程组，负责心跳

    /**
     * 初始化boss线程池
     */
    @Synchronized
    fun initBossLoopGroup() {
        destroyBossLoopGroup()
        bossPool = Executors.newSingleThreadExecutor()
    }

    /**
     * 初始化work线程池
     */
    @Synchronized
    fun initWorkLoopGroup() {
        destroyWorkLoopGroup()
        workPool = Executors.newSingleThreadExecutor()
    }

    /**
     * 执行boss任务
     *
     * @param r
     */
    fun execBossTask(r: Runnable) {
        if (bossPool == null) {
            initBossLoopGroup()
        }
        bossPool?.execute(r)
    }

    /**
     * 执行work任务
     *
     * @param r
     */
    fun execWorkTask(r: Runnable) {
        if (workPool == null) {
            initWorkLoopGroup()
        }
        workPool?.execute(r)
    }

    /**
     * 释放boss线程池
     */
    @Synchronized
    fun destroyBossLoopGroup() {
        bossPool?.let {
            try {
                it.shutdownNow()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                bossPool = null
            }
        }
    }

    /**
     * 释放work线程池
     */
    @Synchronized
    fun destroyWorkLoopGroup() {
        workPool?.let {
            try {
                it.shutdownNow()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                workPool = null
            }
        }
    }

    /**
     * 释放所有线程池
     */
    @Synchronized
    fun destroy() {
        destroyBossLoopGroup()
        destroyWorkLoopGroup()
    }
}