//
// Created by Yorek on 2022/6/10.
//

#ifndef ANDROIDPERFORMANCE_MEMORYTHREAD_H
#define ANDROIDPERFORMANCE_MEMORYTHREAD_H

#include <dlfcn.h>
#include <pthread.h>

#define LOGE(TAG, FMT, args...)
#ifndef LIKELY
#define LIKELY(cond) (__builtin_expect(!!(cond), 1))
#endif

#ifndef UNLIKELY
#define UNLIKELY(cond) (__builtin_expect(!!(cond), 0))
#endif

namespace memory_thread {
    typedef void* (*pthread_routine_t)(void*);

    extern size_t GetStackSize();
    extern void InstallHook();

    extern void OnPThreadCreate(const Dl_info* caller_info, pthread_t* pthread, pthread_attr_t const* attr, void* (*start_routine)(void*), void* args);
}

#endif //ANDROIDPERFORMANCE_MEMORYTHREAD_H
