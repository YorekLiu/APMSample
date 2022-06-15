package xyz.yorek.performance.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import xyz.yorek.performance.utils.Units

object Memory {
    private const val TAG = "Memory"

    fun javaHeapUsage(context: Context) {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        val totalPss = memoryInfo.totalPss

        val runtime = Runtime.getRuntime()
        val javaHeapMax = runtime.maxMemory()
        val javaHeapAlloc = runtime.totalMemory()
        val javaHeapUsed = javaHeapAlloc - runtime.freeMemory()

        val activityManagerMemoryInfo = ActivityManager.MemoryInfo()
        val activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(activityManagerMemoryInfo)
        val totalMem = activityManagerMemoryInfo.totalMem
        val availMem = activityManagerMemoryInfo.availMem

        Log.d(TAG, "totalPss=$totalPss javaHeapMax=$javaHeapMax javaHeapAlloc=$javaHeapAlloc javaHeapUsed=$javaHeapUsed totalMem=${Units.convertB2MB(totalMem)} availMem=${Units.convertB2MB(availMem)}")
    }
}