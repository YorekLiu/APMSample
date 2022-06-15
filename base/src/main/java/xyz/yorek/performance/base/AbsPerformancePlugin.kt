package xyz.yorek.performance.base

import android.app.ActivityManager
import android.content.Context

abstract class AbsPerformancePlugin(
    val context: Context
) {
    abstract fun init()

    open fun start() {

    }

    open fun stop() {

    }
}