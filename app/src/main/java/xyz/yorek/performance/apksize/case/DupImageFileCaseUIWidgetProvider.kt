package xyz.yorek.performance.apksize.case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewDupImageFileBinding

class DupImageFileCaseUIWidgetProvider : CaseUIWidgetProvider() {

    private lateinit var mViewBinding: ViewDupImageFileBinding

    override fun getTitle(): String = "重复图片测试"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewDupImageFileBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {

    }
}