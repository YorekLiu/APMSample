package xyz.yorek.performance.memory.device;

import android.os.Build;
import android.os.Debug;

import java.util.Locale;

public class GCDump {
    public static void start() {
        Debug.startAllocCounting();
    }

    public static void stop() {
        Debug.stopAllocCounting();
    }

    public static void dump(StringBuilder stringBuilder) {
        int allocCount = Debug.getGlobalAllocCount();
        int allocSize = Debug.getGlobalAllocSize();
        int gcInvocationCount = Debug.getGlobalGcInvocationCount();

        stringBuilder.append(String.format(Locale.getDefault(), "allocCount=%d allocSize=%d gcInvocationCount=%d\n", allocCount, allocSize, gcInvocationCount));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 运行的GC次数
            String gcCount = Debug.getRuntimeStat("art.gc.gc-count");
            // GC使用的总耗时，单位是毫秒
            String gcTime = Debug.getRuntimeStat("art.gc.gc-time");
            // 阻塞式GC的次数
            String blockingGcCount = Debug.getRuntimeStat("art.gc.blocking-gc-count");
            // 阻塞式GC的总耗时
            String blockingGcTime = Debug.getRuntimeStat("art.gc.blocking-gc-time");

            stringBuilder.append(String.format(Locale.getDefault(), "gcCount=%s gcTime=%s blockingGcCount=%s blockingGcTime=%s", gcCount, gcTime, blockingGcCount, blockingGcTime));
        }
    }
}
