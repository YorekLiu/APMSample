#include <jni.h>
#include <cstdlib>
#include <cstring>
#include <sys/mman.h>
#include <unistd.h>
#include <android/log.h>

#include "native_leak.h"

extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_memory_case_CacheManagerCaseUIWidgetProvider_allocMemory(JNIEnv *env, jobject thiz) {
    constexpr int _20M = sizeof(char) * 20 * 1024 * 1024;
    int* p = (int *) malloc(_20M);
    memset(p, 0, _20M);
}
extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_memory_case_CacheManagerCaseUIWidgetProvider_allocVSS(JNIEnv *env,
                                                                            jobject thiz) {
    constexpr int _1G =  sizeof(char) * 1024 * 1024 * 1024;
    int* p = (int *) malloc(_1G);
}
extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_memory_case_ProcessCaseUIWidgetProvider_fork(JNIEnv *env, jobject thiz) {
    pid_t pid = fork();
    __android_log_print(ANDROID_LOG_INFO, "AppJNI", "forked pid: %d", pid);
    if (pid < 0) {
        __android_log_print(ANDROID_LOG_INFO, "AppJNI", "fork error: %d", pid);
    } else if (pid == 0) {
        __android_log_print(ANDROID_LOG_INFO, "AppJNI", "fork success, child pid %d - %d", pid, getpid());
        while (true) {
            sleep(5);
        }
    } else {
        __android_log_print(ANDROID_LOG_INFO, "AppJNI", "fork success");
    }
}