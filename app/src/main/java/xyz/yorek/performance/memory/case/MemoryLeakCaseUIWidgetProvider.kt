package xyz.yorek.performance.memory.case

import android.content.Context
import android.content.Intent
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.kwai.koom.javaoom.hprof.ForkStripHeapDumper
import com.tencent.matrix.resource.hproflib.HprofBufferShrinker
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewMemoryLeakBinding
import xyz.yorek.performance.memory.MemoryLeakActivity
import xyz.yorek.performance.utils.TimeCost
import xyz.yorek.performance.utils.Units
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MemoryLeakCaseUIWidgetProvider : CaseUIWidgetProvider() {
    init {
        System.loadLibrary("appcase")
    }

    private lateinit var mViewBinding: ViewMemoryLeakBinding

    override fun getTitle(): String = "内存泄露&OOM检测&堆转储文件裁剪"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewMemoryLeakBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        // 原始hprof文件 154MB -> shark shrink裁剪效果不明显(少了不足1M)/matrix 26M -> zipped 7M
        // koom 写时裁剪 17M -> zipped 3M -> 还原后142.5M
        // origin: 1590 0 57935 59915038 128618760 571736811
        // shark:  1590 0 57935 59915038 128618760 571736811
        // matrix: 1586 0 45910 59915038 1505172 64985790
        // koom will shrink itself:
        // koom:   1617 0 36894 59865962 128060711 572008816
        mViewBinding.btnMemoryLeakTest.setOnClickListener {
            mContext.startActivity(Intent(mContext, MemoryLeakActivity::class.java))
        }

//        mViewBinding.btnNativeMemoryLeakTest.setOnClickListener {
//            // demangler
//            // c++filt -n _Znwj
//            // http://demangler.com/
//            // http://www.kegel.com/mangle.html
//            nativeLeak()
//        }

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
            ForkStripHeapDumper.getInstance().dump(getStrippedHprofFile().absolutePath)
        }

        mViewBinding.btnZipHprof.setOnClickListener {
            zipHprof()
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

    private fun getStrippedHprofZipFile(): File {
        return File(mContext.getExternalFilesDir(null), "test_stripped.zip")
    }

    private fun zipHprof() {
        val strippedFile = getStrippedHprofFile()
        if (strippedFile.exists() && strippedFile.isFile) {
            if (!strippedFile.canRead()) {
                strippedFile.setReadable(true)
            }
            try {
                val zippedFile = getStrippedHprofZipFile()
                val fos = FileOutputStream(zippedFile)
                val zos = ZipOutputStream(fos)

                val fis = FileInputStream(strippedFile)
                val entry = ZipEntry(strippedFile.name)
                entry.method = ZipEntry.DEFLATED
                zos.putNextEntry(entry)

                val byteArray = ByteArray(4096)
                var length: Int
                while (fis.read(byteArray).apply { length = this } >= 0) {
                    zos.write(byteArray, 0, length)
                }
                zos.close()
                fis.close()
                fos.close()

                Log.d(TAG, "strippedFile size=${Units.convertB2MB(strippedFile.length())}MB, zippedFile size=${Units.convertB2MB(zippedFile.length())}MB")
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}