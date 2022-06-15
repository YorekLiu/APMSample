package xyz.yorek.performance.memory

import android.content.Context
import android.content.Intent
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.kwai.koom.base.MonitorLog
import com.kwai.koom.base.MonitorManager
import com.kwai.koom.javaoom.hprof.ForkStripHeapDumper
import com.kwai.koom.javaoom.monitor.OOMHprofUploader
import com.kwai.koom.javaoom.monitor.OOMMonitor
import com.kwai.koom.javaoom.monitor.OOMMonitorConfig
import com.kwai.koom.javaoom.monitor.OOMReportUploader
import com.tencent.matrix.resource.hproflib.HprofBufferShrinker
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewMemoryLeakBinding
import xyz.yorek.performance.utils.TimeCost
import xyz.yorek.performance.utils.Units
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class MemoryLeakCaseUIWidgetProvider : CaseUIWidgetProvider() {
    private lateinit var mViewBinding: ViewMemoryLeakBinding

    override fun getTitle(): String = "内存泄露&OOM检测"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewMemoryLeakBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        initKOOM()

        // 原始hprof文件 154MB -> shark shrink裁剪效果不明显(少了不足1M)/matrix 26M
        // koom 写时裁剪 17M -> 还原后142.5M
        // origin: 1590 0 57935 59915038 128618760 571736811
        // shark:  1590 0 57935 59915038 128618760 571736811
        // matrix: 1586 0 45910 59915038 1505172 64985790
        // koom must be cropped:
        // koom:   1617 0 36894 59865962 128060711 572008816
        mViewBinding.btnMemoryLeakTest.setOnClickListener {
            mContext.startActivity(Intent(mContext, MemoryLeakActivity::class.java))
        }

        mViewBinding.btnDumpHprof.setOnClickListener {
            dumpHprof()
        }

        mViewBinding.btnShrinkHprof.setOnClickListener {
            stripHprofShark()
        }

        mViewBinding.btnMatrixShrink.setOnClickListener {
            stripHprofMatrix()
        }

        mViewBinding.btnKoomDumpAndShrink.setOnClickListener {
            ForkStripHeapDumper.getInstance().dump(getHprofFile().absolutePath)
        }
    }

    private fun dumpHprof() {
        val futureTask = FutureTask {
            val file = getHprofFile()
            Debug.dumpHprofData(file.absolutePath)
            file
        }
        Executors.newCachedThreadPool().submit(futureTask)
//        val file: File? = futureTask.get(5, TimeUnit.SECONDS)
//        Log.d(TAG, "hprof file=${file?.absolutePath}")
    }

    private fun stripHprofShark() {
        val hprofFile = getHprofFile()
        if (hprofFile.exists() && hprofFile.isFile) {
            Log.d(TAG, "hprofFile size: ${Units.convertB2MB(hprofFile.length())}MB")
            val hprofPrimitiveArrayStripper = Class.forName("shark.HprofPrimitiveArrayStripper").newInstance()
            val stripPrimitiveArraysMethod = hprofPrimitiveArrayStripper.javaClass.getDeclaredMethod("stripPrimitiveArrays", File::class.java, File::class.java)
            val outputFile = TimeCost.record(name = "strip hprof") {
                stripPrimitiveArraysMethod.invoke(hprofPrimitiveArrayStripper, hprofFile, getStrippedHprofFile()) as File
            }
            Log.d(TAG, "stripped hprofFile size: ${Units.convertB2MB(outputFile.length())}MB")
        }
    }

    private fun stripHprofMatrix() {
        val hprofFile = getHprofFile()
        if (hprofFile.exists() && hprofFile.isFile) {
            Log.d(TAG, "hprofFile size: ${Units.convertB2MB(hprofFile.length())}MB")
            TimeCost.record(name = "strip hprof") {
                HprofBufferShrinker().shrink(hprofFile, getStrippedHprofFile())
            }
            Log.d(TAG, "stripped hprofFile size: ${Units.convertB2MB(getStrippedHprofFile().length())}MB")
        }
    }

    private fun getHprofFile(): File {
        return File(mContext.getExternalFilesDir(null), "test.hprof")
    }

    private fun getStrippedHprofFile(): File {
        return File(mContext.getExternalFilesDir(null), "test_stripped.hprof")
    }

    private fun initKOOM() {
        val config = OOMMonitorConfig.Builder()
            .setThreadThreshold(50) //50 only for test! Please use default value!
            .setFdThreshold(300) // 300 only for test! Please use default value!
            .setHeapThreshold(0.9f) // 0.9f for test! Please use default value!
            .setVssSizeThreshold(1_000_000) // 1_000_000 for test! Please use default value!
            .setMaxOverThresholdCount(1) // 1 for test! Please use default value!
            .setAnalysisMaxTimesPerVersion(3) // Consider use default value！
            .setAnalysisPeriodPerVersion(15 * 24 * 60 * 60 * 1000) // Consider use default value！
            .setLoopInterval(15_000) // 5_000 for test! Please use default value!
            .setEnableHprofDumpAnalysis(true)
            .setHprofUploader(object : OOMHprofUploader {
                override fun upload(file: File, type: OOMHprofUploader.HprofType) {
                    MonitorLog.e(TAG, "todo, upload hprof ${file.name} if necessary")
                }
            })
            .setReportUploader(object : OOMReportUploader {
                override fun upload(file: File, content: String) {
                    MonitorLog.i(TAG, content)
                    MonitorLog.e(TAG, "todo, upload report ${file.name} if necessary")
                }
            })
            .build()

        MonitorManager.addMonitorConfig(config)
        OOMMonitor.startLoop()
    }
}