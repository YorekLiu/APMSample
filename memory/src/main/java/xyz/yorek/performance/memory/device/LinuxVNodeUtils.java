package xyz.yorek.performance.memory.device;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

/**
 * Created by yorek.liu on 2021/7/15
 *
 * @author yorek.liu
 */
public class LinuxVNodeUtils {
    private static final String TAG = "LinuxVNodeUtils";

    public static int[] getCpuCurrentFreq() {
        int[] output = new int[getNumCores()];
        for (int i = 0; i < getNumCores(); i++) {
            output[i] = 0;
            String path = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq";
            String cat = cat(path);
            if (!TextUtils.isEmpty(cat)) {
                try {
                    //noinspection ConstantConditions
                    output[i] = Integer.parseInt(cat) / 1000;
                } catch (Exception ignored) {
                }
            }
        }
        return output;
    }

    private static int getNumCores() {
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return Pattern.matches("cpu[0-9]+", pathname.getName());
                }
            });
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception ignored) {
            // Default to return 1 core
            return 1;
        }
    }

    @Nullable
    public static String cat(String path) {
        if (TextUtils.isEmpty(path)) return null;
        try {
            RandomAccessFile restrictedFile = new RandomAccessFile(path, "r");
            return restrictedFile.readLine();
        } catch (Throwable e) {
            Log.e(TAG,"cat file fail " + Log.getStackTraceString(e));
            return null;
        }
    }

    @Nullable
    public static String readFile(String path) {
        if (TextUtils.isEmpty(path)) return null;
        try {
            RandomAccessFile restrictedFile = new RandomAccessFile(path, "r");
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = restrictedFile.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Throwable e) {
            Log.e(TAG,"read file fail " + Log.getStackTraceString(e));
            return null;
        }
    }

    public static long parseLong(final String string, final long def) {
        try {
            return (string == null || string.length() <= 0) ? def : Long.decode(string);
        } catch (NumberFormatException e) {
            Log.w(TAG, "parseLong error: " + e.getMessage());
        }
        return def;
    }
}
