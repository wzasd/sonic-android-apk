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
package org.cloud.sonic.android

import android.net.LocalServerSocket
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.cloud.sonic.android.databinding.ActivityMainBinding
import org.cloud.sonic.android.service.SonicManagerService
import org.cloud.sonic.android.utils.SocketManager
import org.cloud.sonic.android.utils.SocketServerManager
import org.cloud.sonic.android.utils.appGlobalScope

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    immersionBar {
      statusBarColor(R.color.auto_bg)
      navigationBarColor(R.color.auto_bg)
      statusBarDarkFont(true)
      autoDarkModeEnable(true)
    }

//    Handler(Looper.getMainLooper()) {
//      finish()
//      false
//    }.sendEmptyMessageDelayed(0, 1500)
    binding.version.text = AppUtils.getAppVersionName()

    appGlobalScope.launch(Dispatchers.IO) {
      SocketServerManager.createServer()

    }

    Handler(Looper.getMainLooper()) {
      appGlobalScope.launch(Dispatchers.IO) {
        SocketManager.connectServer()
      }
      false
    }.sendEmptyMessageDelayed(0, 1500)
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }
}
