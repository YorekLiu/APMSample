package xyz.yorek.performance.memory.case

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import xyz.yorek.performance.PerformancePluginService
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.base.ITick
import xyz.yorek.performance.databinding.ViewCacheManagerBinding
import xyz.yorek.performance.memory.MemoryPlugin

class CacheManagerCaseUIWidgetProvider : CaseUIWidgetProvider(), ITick {

    init {
        System.loadLibrary("appcase")
    }

    object TestCache {
        private val sMemoryKeeper = mutableListOf<ByteArray>()

        fun add(byteArray: ByteArray) {
            sMemoryKeeper.add(byteArray)
        }

        fun clear() {

            sMemoryKeeper.clear()
        }
    }

    private lateinit var mViewBinding: ViewCacheManagerBinding
    private var mMemoryPlugin: MemoryPlugin? = null

    private val mComponentCallbacks = object: ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {}

        override fun onLowMemory() {
            Log.d(TAG, "onLowMemory")
            TestCache.clear()
        }

        override fun onTrimMemory(level: Int) {
            // pss不足时才会出发，与Java堆内存无关
            Log.d(TAG, "onTrimMemory level=$level")
            TestCache.clear()
        }
    }

    override fun getTitle(): String = "缓存管理"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewCacheManagerBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        mMemoryPlugin = PerformancePluginService.find(MemoryPlugin::class.java)

        mViewBinding.btnAllocMemory.setOnClickListener {
            TestCache.add(ByteArray(20 * 1024 * 1024))
        }
        mViewBinding.btnAllocNativeMemory.setOnClickListener {
            allocMemory()
        }
        mViewBinding.btnAllocVSS.setOnClickListener {
            allocVSS()
        }

        mViewBinding.root.context.registerComponentCallbacks(mComponentCallbacks)
    }

    override fun onDestroy() {
        mViewBinding.root.context.unregisterComponentCallbacks(mComponentCallbacks)

    }

    override fun onTick() {
        val memInfo = mMemoryPlugin?.printMemoryInfo()
        mViewBinding.root.post {
            mViewBinding.textMemoryInfo.text = memInfo
        }
    }

    private external fun allocMemory()

    private external fun allocVSS()
}