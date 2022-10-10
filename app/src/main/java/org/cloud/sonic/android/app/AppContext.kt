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

package org.cloud.sonic.android.app

import android.annotation.SuppressLint
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Looper
import org.cloud.sonic.android.utils.SLog

object AppContext {
    fun prepareMainLooper() {
        Looper.prepareMainLooper()
    }

    @SuppressLint("PrivateApi,DiscouragedPrivateApi")
    fun fillAppInfo() {
        try {
            // ActivityThread activityThread = new ActivityThread();
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThreadConstructor = activityThreadClass.getDeclaredConstructor()
            activityThreadConstructor.isAccessible = true
            val activityThread = activityThreadConstructor.newInstance()

            // ActivityThread.sCurrentActivityThread = activityThread;
            val sCurrentActivityThreadField =
                activityThreadClass.getDeclaredField("sCurrentActivityThread")
            sCurrentActivityThreadField.isAccessible = true
            sCurrentActivityThreadField[null] = activityThread

            // ActivityThread.AppBindData appBindData = new ActivityThread.AppBindData();
            val appBindDataClass = Class.forName("android.app.ActivityThread\$AppBindData")
            val appBindDataConstructor = appBindDataClass.getDeclaredConstructor()
            appBindDataConstructor.isAccessible = true
            val appBindData = appBindDataConstructor.newInstance()
            val applicationInfo = ApplicationInfo()
            applicationInfo.packageName = "com.genymobile.scrcpy"

            // appBindData.appInfo = applicationInfo;
            val appInfoField = appBindDataClass.getDeclaredField("appInfo")
            appInfoField.isAccessible = true
            appInfoField[appBindData] = applicationInfo

            // activityThread.mBoundApplication = appBindData;
            val mBoundApplicationField = activityThreadClass.getDeclaredField("mBoundApplication")
            mBoundApplicationField.isAccessible = true
            mBoundApplicationField[activityThread] = appBindData

            // Context ctx = activityThread.getSystemContext();
            val getSystemContextMethod = activityThreadClass.getDeclaredMethod("getSystemContext")
            val ctx = getSystemContextMethod.invoke(activityThread) as Context
            val app = Instrumentation.newApplication(Application::class.java, ctx)

            // activityThread.mInitialApplication = app;
            val mInitialApplicationField =
                activityThreadClass.getDeclaredField("mInitialApplication")
            mInitialApplicationField.isAccessible = true
            mInitialApplicationField[activityThread] = app
        } catch (throwable: Throwable) {
            // this is a workaround, so failing is not an error
            SLog.d("Could not fill app info: " + throwable.message)
        }
    }
}