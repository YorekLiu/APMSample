package xyz.yorek.performance

import android.app.Application
import com.kwai.koom.base.DefaultInitTask
import xyz.yorek.performance.memory.MemoryPlugin

class PerformanceApp : Application() {
    override fun onCreate() {
        super.onCreate()

        PerformancePluginService.registerPlugin(MemoryPlugin::class.java)
        PerformancePluginService.init(this)
        PerformancePluginService.start()

        // KOOM
        DefaultInitTask.init(this)
    }
}