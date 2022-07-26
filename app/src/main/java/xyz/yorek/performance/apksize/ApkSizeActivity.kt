package xyz.yorek.performance.apksize

import xyz.yorek.performance.apksize.case.DupImageFileCaseUIWidgetProvider
import xyz.yorek.performance.apksize.case.FixedResEntryNameCaseUIWidgetProvider
import xyz.yorek.performance.base.BasePerformanceActivity
import xyz.yorek.performance.base.CaseUIWidgetProvider

class ApkSizeActivity : BasePerformanceActivity() {

    override fun getCaseUIWidgetProviderList(): List<Class<out CaseUIWidgetProvider>> {
        return listOf(
            DupImageFileCaseUIWidgetProvider::class.java,
            FixedResEntryNameCaseUIWidgetProvider::class.java,
        )
    }
}