package xyz.yorek.performance.memory

import android.content.Context
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.PerformancePluginService
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewThreadBinding
import xyz.yorek.performance.utils.Units

class ThreadCaseUIWidgetProvider : CaseUIWidgetProvider() {
    private lateinit var mViewBinding: ViewThreadBinding
    private var mMemoryPlugin: MemoryPlugin? = null

    override fun getTitle(): String = "线程模型"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewThreadBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        mMemoryPlugin = PerformancePluginService.find(MemoryPlugin::class.java) ?: return

        mViewBinding.textThreadStack.text = "${Units.convertB2KB(mMemoryPlugin!!.getStackSize())}KB"

        mViewBinding.btnTrimStack.setOnClickListener {
            mMemoryPlugin?.trimThreadStack()
        }

        mViewBinding.btnStartThread.setOnClickListener {
            for (i in 0..100) {
                HandlerThread("test${i}").start()
            }
        }
    }
}