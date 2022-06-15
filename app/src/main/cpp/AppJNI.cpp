#include <jni.h>
#include <cstdlib>
#include <cstring>
#include <sys/mman.h>

extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_memory_CacheManagerCaseUIWidgetProvider_allocMemory(JNIEnv *env, jobject thiz) {
    int* p = (int *) malloc(sizeof(char) * 20 * 1024 * 1024);
    memset(p, 0, 20 * 1024 * 1024);
}
extern "C"
JNIEXPORT void JNICALL
Java_xyz_yorek_performance_memory_CacheManagerCaseUIWidgetProvider_allocVSS(JNIEnv *env,
                                                                            jobject thiz) {
    (int *) malloc(sizeof(char) * 500 * 1024 * 1024);
}