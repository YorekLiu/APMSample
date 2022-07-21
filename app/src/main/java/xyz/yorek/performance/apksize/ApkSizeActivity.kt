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

    /**
     * asset 模式下
     * 压缩libs文件的task需要后于stripDebugSymbols
     * 且要在mergeAssets与compressAssets之间
     * 由于assets在编译时插入，会打不进去，所以也会放一个占位的空文件
     * so文件到压缩时也需要弄一个空的占位文件
     */

    /**
     * zip模式
     * 抽离libs的时机与上面类似，不同的是lib需要上传到服务器
     * 应用启动后在需要用到so之前，下载并放置到指定位置
     * 此模式是基于上面模式的改进
     */

    /**
     * TransformManager.CONTENT_RESOURCES并不会处理到resources资源，而是处理的kotlin/collections/_SetsKt.kotlin_metadata这种类似的文件
     * 达不到我们想要的效果
     * 自定义Task处理merged_res也行不通，三方lib里面的res不在这里
     *
     * 可以使用AnsResGuard的方式，
     */
}