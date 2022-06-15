package xyz.yorek.performance.memory.device;

import android.os.Debug;
import android.os.SystemClock;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yorek.liu on 2021/7/20
 *
 * @author yorek.liu
 */
public class MemoryDump {
    private static final String TAG = "MemoryDump";

    public static void dump(StringBuilder stringBuilder) {
        long begin = SystemClock.uptimeMillis();
        try {
            float[] memInfo = getDeviceMemoryInfo();
            float[] processStatus = getProcessStatus();

            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
            int totalPss = memoryInfo.getTotalPss();

            Runtime runtime = Runtime.getRuntime();
            long javaMax = runtime.maxMemory();
            long javaTotal = runtime.totalMemory();
            long javaUsed = javaTotal - runtime.freeMemory();
            float proportion = (float) javaUsed / javaMax;

            final int B2MB = 1024 * 1024;
            final int KB2MB = 1024;
//            stringBuilder.append("\n-------------------------- memory --------------------------\n");
            stringBuilder.append(
                    String.format(Locale.getDefault(), "MemTotal=%.2fGB MemFree=%.2fGB MemAvailable=%.2fGB\n", memInfo[0], memInfo[1], memInfo[2])
            );
            stringBuilder.append(
                    String.format(Locale.getDefault(), "VSS=%.2fGB RSS=%.2fGB Threads=%.0f\n", processStatus[0], processStatus[1], processStatus[2])
            );
            stringBuilder.append(
                    String.format(Locale.getDefault(), "totalPss=%dMB dalvikPss=%dMB nativePss=%dMB otherPss=%dMB\n", totalPss / KB2MB, memoryInfo.dalvikPss / KB2MB, memoryInfo.nativePss / KB2MB, memoryInfo.otherPss / KB2MB)
            );
            stringBuilder.append(
                    String.format(Locale.getDefault(), "javaMax=%dMB javaUsed=%dMB percent=%.0f%%", javaMax / B2MB, javaUsed / B2MB, proportion * 100)
            );
//            stringBuilder.append("-------------------------- memory --------------------------\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            stringBuilder.append("dump memory info costs: ").append(SystemClock.uptimeMillis() - begin).append("ms\n");
        }
    }

    public static float[] getDeviceMemoryInfo() {
        final float KB2G = 1024 * 1024F;
        float[] mem = new float[3];
        String path = "/proc/meminfo";
        RandomAccessFile restrictedFile = null;
        try {
            restrictedFile = new RandomAccessFile(path, "r");
            String line;
            while ((line = restrictedFile.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    // MemTotal:        5876688 kB
                    Pattern pattern = Pattern.compile("MemTotal:\\s+(\\d*)\\s+.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        mem[0] = Integer.parseInt(matcher.group(1)) / KB2G;
                    }
                } else if (line.startsWith("MemFree:")) {
                    // MemFree:        5876688 kB
                    Pattern pattern = Pattern.compile("MemFree:\\s+(\\d*)\\s+.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        mem[1] = Integer.parseInt(matcher.group(1)) / KB2G;
                    }
                } else if (line.startsWith("MemAvailable:")) {
                    // MemAvailable:        5876688 kB
                    Pattern pattern = Pattern.compile("MemAvailable:\\s+(\\d*)\\s+.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        mem[2] = Integer.parseInt(matcher.group(1)) / KB2G;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (restrictedFile != null) {
                try {
                    restrictedFile.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return mem;
    }

    public static float[] getProcessStatus() {
        final float KB2G = 1024 * 1024F;
        float[] processStatus = new float[3];
        String path = "/proc/self/status";
        RandomAccessFile restrictedFile = null;
        try {
            restrictedFile = new RandomAccessFile(path, "r");
            String line;
            while ((line = restrictedFile.readLine()) != null) {
                if (line.startsWith("VmSize:")) {
                    // MemTotal:        5876688 kB
                    Pattern pattern = Pattern.compile("VmSize:\\s+(\\d*)\\s+.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        processStatus[0] = Integer.parseInt(matcher.group(1)) / KB2G;
                    }
                } else if (line.startsWith("VmRSS:")) {
                    // MemFree:        5876688 kB
                    Pattern pattern = Pattern.compile("VmRSS:\\s+(\\d*)\\s+.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        processStatus[1] = Integer.parseInt(matcher.group(1)) / KB2G;
                    }
                } else if (line.startsWith("Threads:")) {
                    // MemAvailable:        5876688 kB
                    Pattern pattern = Pattern.compile("Threads:\\s+(\\d*)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        processStatus[2] = Integer.parseInt(matcher.group(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (restrictedFile != null) {
                try {
                    restrictedFile.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return processStatus;
    }

    public static float getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long javaMax = runtime.maxMemory();
        long javaTotal = runtime.totalMemory();
        long javaUsed = javaTotal - runtime.freeMemory();

        return (float) javaUsed / javaMax;
    }

    public static String getMemoryUsageString() {
        final int B2MB = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        long javaMax = runtime.maxMemory();
        long javaTotal = runtime.totalMemory();
        long javaUsed = javaTotal - runtime.freeMemory();

        return String.format(Locale.getDefault(), "javaMax=%dMB javaUsed=%dMB", javaMax / B2MB, javaUsed / B2MB);
    }
}
