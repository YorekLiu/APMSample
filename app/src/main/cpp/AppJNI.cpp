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

#pragma PLTHook
#define PAGE_START(addr) ((addr) & PAGE_MASK)
#define PAGE_END(addr)   (PAGE_START(addr + sizeof(uintptr_t) - 1) + PAGE_SIZE)
#define PAGE_COVER(addr) (PAGE_END(addr) - PAGE_START(addr))

int my_log_print(int prio, const char* tag, const char* fmt, ...) {
    return __android_log_print(ANDROID_LOG_INFO, "AppJNI", "What r u taking about?");
}

void hook(uintptr_t base_addr, int32_t address)
{
    uintptr_t  addr;
    void* new_func = (void *) my_log_print;

    addr = base_addr + address;

    //add write permission
    mprotect((void *)PAGE_START(addr), PAGE_COVER(addr), PROT_READ | PROT_WRITE);

    //replace the function address
    *(void **)addr = new_func;

    //clear instruction cache
    __builtin___clear_cache(static_cast<char *>((void *) PAGE_START(addr)),
                            static_cast<char *>((void *) PAGE_END(addr)));
}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_tools_case_PLTHookCaseUIWidgetProvider_hook(JNIEnv *env, jobject thiz,
                                                                       jlong base_addr,
                                                                       jint address) {
    hook(base_addr, address);
    __android_log_print(ANDROID_LOG_INFO, "AppJNI", "Hello, World!");
}