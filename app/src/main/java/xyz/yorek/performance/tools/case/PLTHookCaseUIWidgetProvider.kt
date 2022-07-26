package xyz.yorek.performance.tools.case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewPltHookBinding

class PLTHookCaseUIWidgetProvider : CaseUIWidgetProvider() {
    init {
        System.loadLibrary("appcase")
    }

    private lateinit var mViewPltHookBinding: ViewPltHookBinding

    override fun getTitle(): String = "PLT Hook示例"

    override fun onCreate(context: Context): View {
        mViewPltHookBinding = ViewPltHookBinding.inflate(LayoutInflater.from(context))
        return mViewPltHookBinding.root
    }

    override fun onInit() {
        mViewPltHookBinding.btnHook.setOnClickListener {
            val soAddr = mViewPltHookBinding.etSoAddr.text.toString()
            val funcAddr = mViewPltHookBinding.etFuncAddr.text.toString()
            hook(soAddr.toLong(16), funcAddr.toInt(16))
        }
    }

    external fun hook(baseAddress: Long, address: Int)
}