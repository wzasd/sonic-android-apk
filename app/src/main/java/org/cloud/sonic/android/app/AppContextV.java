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

package org.cloud.sonic.android.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Looper;

import org.cloud.sonic.android.utils.SLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AppContextV {
    @SuppressWarnings("deprecation")
    public static void prepareMainLooper() {
        // Some devices internally create a Handler when creating an input Surface, causing an exception:
        //   "Can't create handler inside thread that has not called Looper.prepare()"
        // <https://github.com/Genymobile/scrcpy/issues/240>
        //
        // Use Looper.prepareMainLooper() instead of Looper.prepare() to avoid a NullPointerException:
        //   "Attempt to read from field 'android.os.MessageQueue android.os.Looper.mQueue'
        //    on a null object reference"
        // <https://github.com/Genymobile/scrcpy/issues/921>
        Looper.prepareMainLooper();
    }

    @SuppressLint("PrivateApi,DiscouragedPrivateApi")
    public static Application fillAppInfo() {
        prepareMainLooper();
        try {
            // ActivityThread activityThread = new ActivityThread();
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Constructor<?> activityThreadConstructor = activityThreadClass.getDeclaredConstructor();
            activityThreadConstructor.setAccessible(true);
            Object activityThread = activityThreadConstructor.newInstance();

            // ActivityThread.sCurrentActivityThread = activityThread;
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            sCurrentActivityThreadField.set(null, activityThread);

            // ActivityThread.AppBindData appBindData = new ActivityThread.AppBindData();
            Class<?> appBindDataClass = Class.forName("android.app.ActivityThread$AppBindData");
            Constructor<?> appBindDataConstructor = appBindDataClass.getDeclaredConstructor();
            appBindDataConstructor.setAccessible(true);
            Object appBindData = appBindDataConstructor.newInstance();

            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = "org.cloud.sonic.android";

            // appBindData.appInfo = applicationInfo;
            Field appInfoField = appBindDataClass.getDeclaredField("appInfo");
            appInfoField.setAccessible(true);
            appInfoField.set(appBindData, applicationInfo);

            // activityThread.mBoundApplication = appBindData;
            Field mBoundApplicationField = activityThreadClass.getDeclaredField("mBoundApplication");
            mBoundApplicationField.setAccessible(true);
            mBoundApplicationField.set(activityThread, appBindData);

            // Context ctx = activityThread.getSystemContext();
            Method getSystemContextMethod = activityThreadClass.getDeclaredMethod("getSystemContext");
            Context ctx = (Context) getSystemContextMethod.invoke(activityThread);

            Application app = Instrumentation.newApplication(Application.class, ctx);

            // activityThread.mInitialApplication = app;
            Field mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");
            mInitialApplicationField.setAccessible(true);
            mInitialApplicationField.set(activityThread, app);
            return app;
        } catch (Throwable throwable) {
            // this is a workaround, so failing is not an error
            SLog.INSTANCE.e("Could not fill app info: " + throwable.getMessage());
        }
        return null;
    }
}
