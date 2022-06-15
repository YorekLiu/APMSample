package xyz.yorek.performance.memory.device;

import android.content.Context;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.util.Arrays;

/**
 * Created by yorek.liu on 2021/7/20
 *
 * @author yorek.liu
 */
public class ProcessInfoDump {
    private static final String TAG = "ProcessInfoDump";

    public static void dump(@Nullable Context context, StringBuilder stringBuilder) {
        long begin = SystemClock.uptimeMillis();
        try {
            ProcessInfo processInfo = ProcessInfo.getProcessInfo(context);
            processInfo.loadProcStat();
            stringBuilder.append("\n-------------------------- process --------------------------\n");
            stringBuilder.append("cpuFreq=").append(Arrays.toString(LinuxVNodeUtils.getCpuCurrentFreq())).append("\n");
            stringBuilder.append(processInfo.toString()).append("\n");
            stringBuilder.append("-------------------------- process --------------------------\n");
            stringBuilder.append("-------------------------------------------------------------\n");
            stringBuilder.append("-------------------------- thread --------------------------\n");
            for (ProcessInfo.ThreadInfo threadInfo : processInfo.threadInfo) {
                threadInfo.loadProcStat();
                stringBuilder.append(threadInfo.toString()).append("\n");;
            }
            stringBuilder.append("-------------------------- thread --------------------------\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stringBuilder.append("dump process info costs: ").append(SystemClock.uptimeMillis() - begin).append("ms\n");
        }
    }
}
