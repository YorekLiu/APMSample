package xyz.yorek.performance.memory

import android.app.Activity
import android.os.Bundle
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.base.ITick
import xyz.yorek.performance.base.ViewBindingBaseActivity
import xyz.yorek.performance.databinding.ActivityMemoryBinding
import xyz.yorek.performance.databinding.ListItemCaseBinding
import xyz.yorek.performance.memory.case.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MemoryActivity : ViewBindingBaseActivity<ActivityMemoryBinding>() {
    private val mCaseInstanceList = mutableListOf<CaseUIWidgetProvider>()
    private var mExecutorService: ExecutorService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupTicker()
    }

    override fun onDestroy() {
        super.onDestroy()
        mExecutorService?.shutdownNow()
        mCaseInstanceList.forEach { it.onDestroy() }
        mCaseInstanceList.clear()
    }

    private fun initView() {
        getCaseUIWidgetProviderList().forEach {
            val instance = it.getConstructor().newInstance()
            val view = instance.create(this)
            instance.onInit()

            mCaseInstanceList.add(instance)

            val listItemCaseBinding = ListItemCaseBinding.inflate(layoutInflater)
            listItemCaseBinding.text.text = instance.getTitle()
            listItemCaseBinding.container.addView(view)

            mBinding.caseLists.addView(listItemCaseBinding.root)
        }
    }

    private fun getCaseUIWidgetProviderList() = listOf<Class<out CaseUIWidgetProvider>>(
        DevicePerformanceCaseUIWidgetProvider::class.java,
        CacheManagerCaseUIWidgetProvider::class.java,
        ProcessCaseUIWidgetProvider::class.java,
        ThreadCaseUIWidgetProvider::class.java,
        WildThreadCaseUIWidgetProvider::class.java,
        ImageCaseUIWidgetProvider::class.java,
        MemoryLeakCaseUIWidgetProvider::class.java,
    )

    private fun setupTicker() {
        val tickList = mCaseInstanceList.filter { it is ITick }

        if (tickList.isEmpty()) return

        val executorService = Executors.newSingleThreadScheduledExecutor()
        executorService.scheduleAtFixedRate(
            TickRunnable(this, tickList),
            0,
            1,
            TimeUnit.SECONDS
        )

        mExecutorService = executorService
    }

    internal class TickRunnable(
        private val activity: Activity,
        private val tickList: List<CaseUIWidgetProvider>
    ): Runnable {
        override fun run() {
            try {
                tickList.forEach { (it as ITick).onTick() }
            } catch (e : Throwable) {
                e.printStackTrace()
            }
        }
    }
}