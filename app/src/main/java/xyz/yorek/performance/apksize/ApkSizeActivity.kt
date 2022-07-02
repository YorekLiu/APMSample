package xyz.yorek.performance.apksize

import xyz.yorek.performance.apksize.case.DupImageFileCaseUIWidgetProvider
import xyz.yorek.performance.base.BasePerformanceActivity
import xyz.yorek.performance.base.CaseUIWidgetProvider

class ApkSizeActivity : BasePerformanceActivity() {

    override fun getCaseUIWidgetProviderList(): List<Class<out CaseUIWidgetProvider>> {
        return listOf(
            DupImageFileCaseUIWidgetProvider::class.java,
        )
    }

    /**
     * asset 模式下
     * 压缩libs文件的task需要后于stripDebugSymbols
     * 且要在mergeAssets与compressAssets之间
     * 由于assets在编译时插入，会打不进去，所以也会放一个占位的空文件
     * so文件到压缩时也需要弄一个空的占位文件
     */

    /**
     * zip模式
     *
     */
}