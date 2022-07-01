package xyz.yorek.performance.memory.case

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.PerformancePluginService
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewWildThreadBinding
import xyz.yorek.performance.memory.MemoryPlugin

class WildThreadCaseUIWidgetProvider : CaseUIWidgetProvider() {
    private lateinit var mViewBinding: ViewWildThreadBinding
    private var mMemoryPlugin: MemoryPlugin? = null

    override fun getTitle(): String = "野生线程管理"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewWildThreadBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        mMemoryPlugin = PerformancePluginService.find(MemoryPlugin::class.java) ?: return

        mViewBinding.btnStartThreadDirectly.setOnClickListener {
            Thread {
                val threadName = Thread.currentThread().name
                Log.d(TAG, "task running in $threadName >>>> ")
                mViewBinding.root.post {
                    mViewBinding.textThreadName.text = "Runnable运行在${threadName}"
                }
            }.start()
        }
    }
}