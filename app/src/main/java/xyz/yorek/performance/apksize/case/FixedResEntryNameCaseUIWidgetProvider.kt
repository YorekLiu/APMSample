package xyz.yorek.performance.apksize.case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import xyz.yorek.performance.R
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewDupImageFileBinding

class FixedResEntryNameCaseUIWidgetProvider : CaseUIWidgetProvider() {

    override fun getTitle(): String = "资源项名称测试"

    override fun onCreate(context: Context): View {
        return TextView(context)
    }

    override fun onInit() {
        (mView as TextView).text = mContext.resources.getResourceEntryName(R.drawable.ic_retry_duplicate)
    }
}