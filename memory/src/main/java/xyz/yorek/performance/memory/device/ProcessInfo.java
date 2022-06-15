package xyz.yorek.performance.memory.device;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessInfo {
    private static final String TAG = "ProcessInfo";

    public static ProcessInfo getProcessInfo(@Nullable Context context) {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.pid = Process.myPid();
        processInfo.name = context != null ? ActivityThread.currentProcessName() : "default";
        processInfo.threadInfo = ThreadInfo.parseThreadsInfo(processInfo.pid);
        processInfo.upTime = SystemClock.uptimeMillis();
        processInfo.time = System.currentTimeMillis();
        return processInfo;
    }

    int pid;
    String name;
    long time;
    long upTime;
    public long jiffies;
    public List<ThreadInfo> threadInfo = Collections.emptyList();

    public void loadProcStat() throws IOException {
        ProcStatUtil.ProcStat stat = ProcStatUtil.of(pid);
        if (stat != null) {
            name = stat.comm;
            jiffies = stat.getJiffies();
        } else {
            throw new IOException("parse fail: " + LinuxVNodeUtils.cat("/proc/" + pid + "/stat"));
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "process:" + name + "(" + pid + ") " + jiffies + "(j) thread size:" + threadInfo.size();
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static class ThreadInfo {
        private static List<ThreadInfo> parseThreadsInfo(int pid) {
            String rootPath = "/proc/" + pid + "/task/";
            File taskDir = new File(rootPath);
            try {
                if (taskDir.isDirectory()) {
                    File[] subDirs = taskDir.listFiles();
                    if (null == subDirs) {
                        return Collections.emptyList();
                    }

                    List<ThreadInfo> threadInfoList = new ArrayList<>(subDirs.length);
                    for (File file : subDirs) {
                        if (!file.isDirectory()) {
                            continue;
                        }
                        try {
                            ThreadInfo threadInfo = of(pid, Integer.parseInt(file.getName()));
                            threadInfoList.add(threadInfo);
                        } catch (Exception e) {
                            Log.w(TAG, "parse thread error: " + file.getName() + ", " + Log.getStackTraceString(e));
                        }
                    }
                    return threadInfoList;
                }
            } catch (Exception e) {
                Log.w(TAG, "list thread dir error, " + Log.getStackTraceString(e));
            }
            return Collections.emptyList();
        }

        private static ThreadInfo of(int pid, int tid) {
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.pid = pid;
            threadInfo.tid = tid;
            return threadInfo;
        }

        public int pid;
        public int tid;
        public String name;
        public String stat;
        public long jiffies;

        public void loadProcStat() throws IOException {
            ProcStatUtil.ProcStat stat = ProcStatUtil.of(pid, tid);
            if (stat != null && !TextUtils.isEmpty(stat.comm)) {
                this.name = stat.comm;
                this.stat = stat.stat;
                jiffies = stat.getJiffies();
            } else {
                throw new IOException("parse fail: " + LinuxVNodeUtils.cat("/proc/" + pid + "/task/" + tid + "/stat"));
            }
        }

        @NonNull
        @Override
        public String toString() {
            return name + "(" + tid + ")(" + stat + ") " + jiffies + "(j)";
        }
    }
}