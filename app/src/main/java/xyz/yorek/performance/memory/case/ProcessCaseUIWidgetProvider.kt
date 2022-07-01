package xyz.yorek.performance.memory.case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewProcessBinding

class ProcessCaseUIWidgetProvider : CaseUIWidgetProvider() {
    private lateinit var mViewBinding: ViewProcessBinding

    override fun getTitle(): String = "进程模型"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewProcessBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        mViewBinding.btnStartEmptyProcess.setOnClickListener {
            fork()
        }
    }

    private external fun fork()
}