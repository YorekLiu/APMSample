package xyz.yorek.performance.memory.device;

import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.system.Os;
import android.util.Log;

import androidx.core.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yorek.liu on 2021/7/20
 *
 * @author yorek.liu
 */
public class FDDump {
    private static final String TAG = "FDDump";

    public static void dump(StringBuilder stringBuilder) {
        long begin = SystemClock.uptimeMillis();
        try {
            int fdLimit = getFDLimit();
            int fdSize = getFDSize();
            List<Pair<String, String>> fdList = getFDList();
            stringBuilder.append("\n-------------------------- fd --------------------------\n");
            stringBuilder.append(String.format(Locale.getDefault(), "fdLimit=%d     current size=%d\n", fdLimit, fdSize));
            for (Pair<String, String> fdPair : fdList) {
                stringBuilder.append(String.format(Locale.getDefault(), "%s -> %s\n", fdPair.first, fdPair.second));
            }
            stringBuilder.append("-------------------------- fd --------------------------\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stringBuilder.append("dump fd info costs: ").append(SystemClock.uptimeMillis() - begin).append("ms\n");
        }
    }

    public static int getFDLimit() {
        String path = "/proc/" + Process.myPid() + "/limits";
        RandomAccessFile restrictedFile = null;
        try {
            restrictedFile = new RandomAccessFile(path, "r");
            String line;
            while ((line = restrictedFile.readLine()) != null) {
                if (line.startsWith("Max open files")) {
                    // Max open files            32768                32768                files
                    Pattern pattern = Pattern.compile("Max open files\\s+(\\d*)\\s+.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return Integer.parseInt(matcher.group(1));
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
        // Return the number of cores (virtual CPU devices)
        return Integer.MAX_VALUE;
    }

    public static int getFDSize() {
        String fdPath = "/proc/" + Process.myPid() + "/fd";
        File fdDir = new File(fdPath);
        return fdDir.listFiles().length;
    }

    public static List<Pair<String, String>> getFDList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String fdPath = "/proc/" + Process.myPid() + "/fd";
            File fdDir = new File(fdPath);
            File[] fdFiles = fdDir.listFiles();
            final List<Pair<String, String>> fdList = new ArrayList<>(fdFiles.length);
            for (File file : fdFiles) {
                try {
                    String link = Os.readlink(file.getAbsolutePath());
                    fdList.add(new Pair<>(file.getName(), link));
                } catch (Exception e) {
                    Log.d(TAG, "failed to read " + file.getName());
                    fdList.add(new Pair<>(file.getName(), "failed"));
                }
            }
            return fdList;
        }
        return Collections.emptyList();
    }
}
