package xyz.yorek.performance.base

import android.app.Activity
import android.os.Bundle
import xyz.yorek.performance.databinding.ActivityBasePerformanceBinding
import xyz.yorek.performance.databinding.ListItemCaseBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class BasePerformanceActivity : ViewBindingBaseActivity<ActivityBasePerformanceBinding>() {
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

    abstract fun getCaseUIWidgetProviderList(): List<Class<out CaseUIWidgetProvider>>

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