package xyz.yorek.performance.utils

import android.os.SystemClock
import android.util.Log

object TimeCost {
    private const val TAG = "TimeCost"

    fun <T> record(name: String? = null, action: () -> T): T {
        val start = SystemClock.elapsedRealtime()
        val ret = action()
        val end = SystemClock.elapsedRealtime()
        Log.d(TAG, "${name ?: "time"} costs: ${end - start}ms")
        return ret
    }
}