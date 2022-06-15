package xyz.yorek.performance.memory

import android.os.Bundle
import android.util.Log
import xyz.yorek.performance.base.ViewBindingBaseActivity
import xyz.yorek.performance.databinding.ActivityMemoryLeakBinding

class MemoryLeakActivity : ViewBindingBaseActivity<ActivityMemoryLeakBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            while (true) {
                Thread.sleep(10_000)
                Log.d(TAG, "task running in activity >>>>>>>>>>>>> threadName=${Thread.currentThread().name}")
            }
        }.start()
    }
}