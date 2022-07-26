package xyz.yorek.performance.tools

import xyz.yorek.performance.base.BasePerformanceActivity
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.tools.case.PLTHookCaseUIWidgetProvider

class ToolsActivity : BasePerformanceActivity() {

    override fun getCaseUIWidgetProviderList(): List<Class<out CaseUIWidgetProvider>> {
        return listOf(
            PLTHookCaseUIWidgetProvider::class.java,
        )
    }
}