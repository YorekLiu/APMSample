package xyz.yorek.performance.memory

import xyz.yorek.performance.base.BasePerformanceActivity
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.memory.case.*

class MemoryActivity : BasePerformanceActivity() {

    override fun getCaseUIWidgetProviderList(): List<Class<out CaseUIWidgetProvider>> {
        return listOf(
            DevicePerformanceCaseUIWidgetProvider::class.java,
            CacheManagerCaseUIWidgetProvider::class.java,
            ProcessCaseUIWidgetProvider::class.java,
            ThreadCaseUIWidgetProvider::class.java,
            WildThreadCaseUIWidgetProvider::class.java,
            ImageCaseUIWidgetProvider::class.java,
            MemoryLeakCaseUIWidgetProvider::class.java,
        )
    }
}