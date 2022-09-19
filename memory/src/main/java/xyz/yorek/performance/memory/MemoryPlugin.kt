package xyz.yorek.performance.memory

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import xyz.yorek.performance.base.AbsPerformancePlugin
import xyz.yorek.performance.memory.device.DevicePerformance
import xyz.yorek.performance.memory.device.GCDump
import xyz.yorek.performance.memory.device.MemoryDump
import xyz.yorek.performance.memory.thread.ThreadStackTrim
import xyz.yorek.performance.utils.TimeCost
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MemoryPlugin(
    context: Context
) : AbsPerformancePlugin(context) {

    private var mExecutorService: ScheduledExecutorService? = null

    private companion object {
        private const val TAG = "MemoryPlugin"
    }

    override fun init() {
    }

    override fun start() {
        super.start()
        GCDump.start()

        mExecutorService?.shutdownNow()
        mExecutorService = Executors.newSingleThreadScheduledExecutor().apply {
            this.scheduleAtFixedRate(
                Runnable {
                    checkAndTrimJavaHeap()
                },
                15,
                15,
                TimeUnit.SECONDS
            )
        }
    }

    override fun stop() {
        super.stop()

        GCDump.stop()

        mExecutorService?.shutdownNow()
        mExecutorService = null
    }

    fun getDeviceYearClass() = TimeCost.record(name = "getDevicePerformance") {
        DevicePerformance.getDevicePerformance(context)
    }

    fun printMemoryInfo(): String {
        val stringBuilder = StringBuilder()
        MemoryDump.dump(stringBuilder)

        stringBuilder.appendLine()
        GCDump.dump(stringBuilder)

        return stringBuilder.toString()
    }

    fun getStackSize(): Int {
        return TimeCost.record(name = "getStackSize") {
            ThreadStackTrim.getStackSize()
        }
    }

    fun trimThreadStack() {
        ThreadStackTrim.installHook()
    }

    private fun checkAndTrimJavaHeap() {
        val proportion = MemoryDump.getMemoryUsage()
        if (proportion >= 0.85) {
            val application = context.applicationContext as Application
            application.onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL)
        }
    }
    /** hook之后的线程栈空间 520
    77310db000-77310dc000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    77310dc000-77310dd000 ---p 00000000 00:00 0
    77310dd000-773115f000 rw-p 00000000 00:00 0
    773115f000-7731160000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731160000-7731161000 ---p 00000000 00:00 0
    7731161000-77311e3000 rw-p 00000000 00:00 0
    77311e3000-77311e4000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    77311e4000-77311e5000 ---p 00000000 00:00 0
    77311e5000-7731267000 rw-p 00000000 00:00 0
    7731267000-7731268000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731268000-7731269000 ---p 00000000 00:00 0
    7731269000-77312eb000 rw-p 00000000 00:00 0
    77312eb000-77312ec000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    77312ec000-77312ed000 ---p 00000000 00:00 0
    77312ed000-773136f000 rw-p 00000000 00:00 0
    773136f000-7731370000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731370000-7731371000 ---p 00000000 00:00 0
    7731371000-77313f3000 rw-p 00000000 00:00 0
    77313f3000-77313f4000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    77313f4000-77313f5000 ---p 00000000 00:00 0
    77313f5000-7731477000 rw-p 00000000 00:00 0
    7731477000-7731478000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731478000-7731479000 ---p 00000000 00:00 0
     */

    /** Hook前 1040
    7730826000-7730827000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730827000-7730828000 ---p 00000000 00:00 0
    7730828000-773092c000 rw-p 00000000 00:00 0
    773092c000-773092d000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    773092d000-773092e000 ---p 00000000 00:00 0
    773092e000-7730a32000 rw-p 00000000 00:00 0
    7730a32000-7730a33000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730a33000-7730a34000 ---p 00000000 00:00 0
    7730a34000-7730b38000 rw-p 00000000 00:00 0
    7730b38000-7730b39000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730b39000-7730b3a000 ---p 00000000 00:00 0
    7730b3a000-7730c3e000 rw-p 00000000 00:00 0
    7730c3e000-7730c3f000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730c3f000-7730c40000 ---p 00000000 00:00 0
    7730c40000-7730d44000 rw-p 00000000 00:00 0
    7730d44000-7730d45000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730d45000-7730d46000 ---p 00000000 00:00 0
    7730d46000-7730e4a000 rw-p 00000000 00:00 0
    7730e4a000-7730e4b000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730e4b000-7730e4c000 ---p 00000000 00:00 0
    7730e4c000-7730f50000 rw-p 00000000 00:00 0
    7730f50000-7730f51000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7730f51000-7730f52000 ---p 00000000 00:00 0
    7730f52000-7731056000 rw-p 00000000 00:00 0
    7731056000-7731057000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731057000-7731058000 ---p 00000000 00:00 0
    7731058000-773115c000 rw-p 00000000 00:00 0
    773115c000-773115d000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    773115d000-773115e000 ---p 00000000 00:00 0
    773115e000-7731262000 rw-p 00000000 00:00 0
    7731262000-7731263000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731263000-7731264000 ---p 00000000 00:00 0
    7731264000-7731368000 rw-p 00000000 00:00 0
    7731368000-7731369000 ---p 00000000 00:00 0                              [anon:thread stack guard]
    7731369000-773136a000 ---p 00000000 00:00 0
     */

    /**
    Android 12: 1052KB
    778a384000-778a385000 ---p 00000000 00:00 0                              [anon:stack_and_tls:11978]
    778a385000-778a48c000 rw-p 00000000 00:00 0                              [anon:stack_and_tls:11978]
    778a48c000-778a48e000 ---p 00000000 00:00 0
     */

    /**
     * 532kb
    76d8119000-76d811a000 ---p 00000000 00:00 0                              [anon:stack_and_tls:12591]
    76d811a000-76d819f000 rw-p 00000000 00:00 0                              [anon:stack_and_tls:12591]
    76d819f000-76d81a1000 ---p 00000000 00:00 0
     */
}