package org.cloud.sonic.protocol.util

import android.util.Log

object SonicPLog {
    var debug = true
    private const val TAG = "SonicProtocol"
    private const val PREFIX = "[SystemLog] SonicProtocol"

    fun debug(debug1: Boolean) {
        debug = debug1
    }

    fun d(tag: String, o: Any) {
        if (debug) {
            Log.d(TAG, "$tag $o")
            println(PREFIX + "DEBUG: $tag $o")

        }
    }

    fun e(tag: String, o: Any) {
        if (debug) {
            Log.e(TAG, "$tag $o")
            println(PREFIX + "ERROR: $tag $o")
        }
    }
}