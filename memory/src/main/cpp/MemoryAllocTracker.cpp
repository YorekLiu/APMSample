//
// Created by Yorek on 2022/6/23.
//
#include <android/log.h>
#include <unistd.h>
#include "Hooks.h"

#include "MemoryAllocTracker.h"
#include "MemoryThread.h"

//#define TAG "MemoryAllocTracker"
//
//DECLARE_HOOK_ORIG(int, pthread_create, pthread_t*, pthread_attr_t const*, memory_thread::pthread_routine_t, void*);
//
//DEFINE_HOOK_FUN(int, pthread_create,
//                pthread_t* pthread, pthread_attr_t const* attr, memory_thread::pthread_routine_t start_routine, void* args) {
//    Dl_info callerInfo = {};
//    bool callerInfoOk = true;
//    if (dladdr(__builtin_return_address(0), &callerInfo) == 0) {
//        __android_log_print(ANDROID_LOG_INFO, TAG, "%d >> Fail to get caller info.", ::getpid());
//        callerInfoOk = false;
//    }
//
//    pthread_attr_t tmpAttr;
//    if (LIKELY(attr == nullptr)) {
//        int ret = pthread_attr_init(&tmpAttr);
//        if (UNLIKELY(ret != 0)) {
//            __android_log_print(ANDROID_LOG_INFO, TAG, "Fail to init new attr, ret: %d", ret);
//        }
//    } else {
//        tmpAttr = *attr;
//    }
//
//    if (callerInfoOk) {
//        memory_thread::OnPThreadCreate(&callerInfo, pthread, &tmpAttr, start_routine, args);
//    }
//
//    CALL_ORIGIN_FUNC_RET(int, tmpRet, pthread_create, pthread, &tmpAttr, start_routine, args);
//    int ret = tmpRet;
//
//    if (LIKELY(attr == nullptr)) {
//        pthread_attr_destroy(&tmpAttr);
//    }
//
//    return ret;
//}

namespace memory_alloc_tracker {
    void InstallHook() {
//        FETCH_ORIGIN_FUNC(_Znaj)
//        FETCH_ORIGIN_FUNC(_ZdlPvj)
    }
}

