//
// Created by Yorek on 2022/6/10.
//

#include <cstddef>
#include <pthread.h>
#include <android/log.h>
#include <cstring>
#include <xhook.h>
#include <unistd.h>
#include <bits/glibc-syscalls.h>
#include "Hooks.h"
#include "MemoryThread.h"

#define TAG "memory_thread"
#define ORIGINAL_LIB "libc.so"

DECLARE_HOOK_ORIG(int, pthread_create, pthread_t*, pthread_attr_t const*, memory_thread::pthread_routine_t, void*);

DEFINE_HOOK_FUN(int, pthread_create,
                pthread_t* pthread, pthread_attr_t const* attr, memory_thread::pthread_routine_t start_routine, void* args) {
    Dl_info callerInfo = {};
    bool callerInfoOk = true;
    if (dladdr(__builtin_return_address(0), &callerInfo) == 0) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "%d >> Fail to get caller info.", ::getpid());
        callerInfoOk = false;
    }

    pthread_attr_t tmpAttr;
    if (LIKELY(attr == nullptr)) {
        int ret = pthread_attr_init(&tmpAttr);
        if (UNLIKELY(ret != 0)) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Fail to init new attr, ret: %d", ret);
        }
    } else {
        tmpAttr = *attr;
    }

    if (callerInfoOk) {
        memory_thread::OnPThreadCreate(&callerInfo, pthread, &tmpAttr, start_routine, args);
    }

    CALL_ORIGIN_FUNC_RET(int, tmpRet, pthread_create, pthread, &tmpAttr, start_routine, args);
    int ret = tmpRet;

    if (LIKELY(attr == nullptr)) {
        pthread_attr_destroy(&tmpAttr);
    }

    return ret;
}

namespace memory_thread {
    size_t GetStackSize() {
        size_t stackSize = 0;
        pthread_attr_t attr;

        pthread_attr_init(&attr);
        pthread_attr_getstacksize(&attr, &stackSize);

//        __android_log_print(ANDROID_LOG_INFO, TAG, "stackSize=%uB", stackSize);

        return stackSize;
    }

    void InstallHook() {
        FETCH_ORIGIN_FUNC(pthread_create)

        xhook_enable_debug(1);
        xhook_clear();
        xhook_register(".*/.*\\.so$", "pthread_create",
                       (void *) HANDLER_FUNC_NAME(pthread_create), nullptr);
        xhook_refresh(0);
    }

    static int Trim2Half(pthread_attr_t* attr) {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "automatic Trim2Half is called");
        size_t stackSize = 0;

        pthread_attr_getstacksize(attr, &stackSize);

        return pthread_attr_setstacksize(attr, stackSize >> 1U);
    }

    static bool strEndsWith(const char* str, const char* suffix) {
        if (str == nullptr) {
            return false;
        }
        if (suffix == nullptr) {
            return false;
        }
        size_t strLen = ::strlen(str);
        size_t suffixLen = ::strlen(suffix);
        if (strLen < suffixLen) {
            return false;
        }
        return ::strncmp(str + strLen - suffixLen, suffix, suffixLen) == 0;
    }

    void OnPThreadCreate(const Dl_info* caller_info, pthread_t* pthread_ptr, pthread_attr_t const* attr, void* (*start_routine)(void*), void* args) {
        if (attr == nullptr) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "attr is null, skip adjusting.");
            return;
        }

        if (strEndsWith(caller_info->dli_fname, "/libhwui.so")) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Inside libhwui.so, skip adjusting.");
            return;
        }

        int ret = Trim2Half(const_cast<pthread_attr_t*>(attr));
        if (UNLIKELY(ret != 0)) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Fail to adjust stack size, ret: %d", ret);
        }
    }
}