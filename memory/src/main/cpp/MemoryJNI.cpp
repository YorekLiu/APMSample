#include <jni.h>
#include <xhook.h>

#include "MemoryThread.h"
#include "AnrLogPrinter.h"

extern "C"
JNIEXPORT jint JNICALL
Java_xyz_yorek_performance_memory_thread_ThreadStackTrim_getStackSize(JNIEnv *env, jobject thiz) {
    return (jint) memory_thread::GetStackSize();
}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_memory_thread_ThreadStackTrim_installHook(JNIEnv *env, jobject thiz) {
    memory_thread::InstallHook();
}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_tools_AnrLogPrinter_print(JNIEnv *env, jobject thiz) {
    auto printer = anr_log_printer::AnrLogPrinter();
    printer.print();
}