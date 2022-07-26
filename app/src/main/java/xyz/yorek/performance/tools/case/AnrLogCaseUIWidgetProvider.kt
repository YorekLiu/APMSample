package xyz.yorek.performance.tools.case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewAnrLogBinding

class AnrLogCaseUIWidgetProvider : CaseUIWidgetProvider() {
    private lateinit var mViewAnrLogBinding: ViewAnrLogBinding

    override fun getTitle(): String = "获取ANR日志"

    override fun onCreate(context: Context): View {
        mViewAnrLogBinding = ViewAnrLogBinding.inflate(LayoutInflater.from(context))
        return mViewAnrLogBinding.root
    }

    override fun onInit() {
        mViewAnrLogBinding.btnPrintLog.setOnClickListener {
            printAnrLog()
        }
    }

    external fun printAnrLog()
}