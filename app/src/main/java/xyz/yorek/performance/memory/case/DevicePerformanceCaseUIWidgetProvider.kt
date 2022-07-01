package xyz.yorek.performance.memory.case

import android.content.Context
import android.view.View
import android.widget.TextView
import xyz.yorek.performance.PerformancePluginService
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.memory.MemoryPlugin

class DevicePerformanceCaseUIWidgetProvider : CaseUIWidgetProvider() {

    override fun getTitle(): String = "设备性能等级"

    override fun onCreate(context: Context): View {
        return TextView(context)
    }

    override fun onInit() {
        val memoryPlugin = PerformancePluginService.find(MemoryPlugin::class.java)
        (mView as TextView).text = memoryPlugin?.getDeviceYearClass()?.name ?: "--"
    }
}