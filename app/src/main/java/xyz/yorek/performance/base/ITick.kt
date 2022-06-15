package xyz.yorek.performance.base

import androidx.annotation.WorkerThread

interface ITick {
    @WorkerThread
    fun onTick()
}