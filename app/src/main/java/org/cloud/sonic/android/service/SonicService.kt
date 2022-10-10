/*
 *  Copyright (C) [SonicCloudOrg] Sonic Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.cloud.sonic.android.service

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Looper
import com.blankj.utilcode.util.NetworkUtils
import com.thanosfisherman.wifiutils.WifiUtils
import org.cloud.sonic.android.App
import org.cloud.sonic.android.app.AppContext
import org.cloud.sonic.android.app.AppContextV
import org.cloud.sonic.android.model.SonicWifiInfo
import org.cloud.sonic.android.model.SonicWifiPacket
import org.cloud.sonic.android.plugin.SonicPluginAppList
import org.cloud.sonic.android.plugin.SonicPluginWifiManager
import org.cloud.sonic.android.utils.SLog
import java.lang.reflect.Method

class SonicService {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Looper.prepare()

      Thread {
        SLog.i("init")
        SonicService()
      }.start()
      Looper.loop()

    }
  }
  var context: Context? = null
  var appListPlugin: SonicPluginAppList? = null
  var wifiManager: SonicPluginWifiManager? = null
  init {
    SLog.i("create context ？")
    context = getContextForNew()
    SLog.i("context ？")
    context?.let {
      SLog.i("context is not empty")

      appListPlugin = SonicPluginAppList(it)
        wifiManager = SonicPluginWifiManager(it)

        WifiUtils.withContext(it).scanWifi { results ->
          val mWifiManager =
            it.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
          val wifiInfo = mWifiManager.connectionInfo
          val wifiInfos: MutableList<SonicWifiInfo> = ArrayList()

          if (results.isEmpty()) {
            SLog.i("SCAN RESULTS IT'S EMPTY")
          } else {
            SLog.i("GOT SCAN RESULTS $results")
            for (result in results) {
              wifiInfos.add(SonicWifiInfo.transform(result))
            }
          }

          val sendPacket = SonicWifiPacket(
            isConnectWifi = NetworkUtils.isWifiConnected(),
            connectedWifi = SonicWifiInfo.transform(wifiInfo),
            wifiResults = wifiInfos
          )
        }
      }
  }

  private fun getContextForNew(): Context? {
    try {
      SLog.i("create context 1")
      val activityThread = Class.forName("android.app.ActivityThread")
      SLog.i("create context 2")
      val systemMain: Method = activityThread.getDeclaredMethod("systemMain")
      SLog.i("create context 3")
      val objectSystemMain: Any = systemMain.invoke(null)
      SLog.i("create context 4")
      val contextImpl = Class.forName("android.app.ContextImpl")
      SLog.i("create context 5")
      val createSystemContext: Method =
        contextImpl.getDeclaredMethod("createSystemContext", activityThread)
      createSystemContext.isAccessible = true
      val contextInstance: Context = createSystemContext.invoke(null, objectSystemMain) as Context
      return contextInstance.createPackageContext(
        "org.cloud.sonic.android",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
      )
    }catch (e:Exception) {
      SLog.e("error", e)
    }
    return null

  }
}
