//
// Created by Yorek on 2022/6/23.
//
#include <android/log.h>
#include <cstring>

#include "native_leak.h"

namespace native_leak {
    void leak() {
        int n = 100;
        int* p = new int[n];
        int* c = new int[n];
        memset(p, 100, sizeof(int) * n);
        memcpy(c, p, sizeof(int) * n);

        delete[] p;
    }
}