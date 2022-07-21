package xyz.yorek.performance.apksize

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import com.facebook.soloader.DirectorySoSource
import com.facebook.soloader.SoLoader
import com.facebook.soloader.SoSource
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object SoDynamicLoader {
    private lateinit var sContext: Application
    private var sExtracted = false

    fun init(context: Application) {
        sContext = context
        try {
            SoLoader.init(context, SoLoader.SOLOADER_ALLOW_ASYNC_INIT);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private const val NATIVE_DIR = "dllibs"
    private const val PACKED_APP_LIBS_NAME = "libs_asset.zip"
    val SO_MODE = Mode.NONE

    enum class Mode(
        val label: String
    ) {
        NONE("none"),
        ASSET("asset"),
        ZIP("zip"),
    }

    @WorkerThread
    fun extract(complete: () -> Unit) {
        if (sExtracted || SO_MODE == Mode.NONE) {
            complete()
            return
        }

        sExtracted = true

        val outputDir = getDir(sContext)
        if (SO_MODE == Mode.ASSET) {
            val assetManager = sContext.assets
            val inputStream = assetManager.open(PACKED_APP_LIBS_NAME)
            val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
            var zipEntry: ZipEntry?
            while ((zipInputStream.nextEntry.also { zipEntry = it }) != null) {
                val entry = zipEntry!!
                val zipEntryFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    zipEntryFile.mkdirs()
                } else {
                    Util.copyFileUsingStream(zipInputStream, zipEntryFile)
                }
                zipInputStream.closeEntry()
            }
            zipInputStream.close()
        } else { // Mode.ZIP
            // 需要下载so文件到outputDir目录下
            // 测试时可以先push到/data/data/xyz.yorek.performance/app_dllibs
            // push后注意给到so 644的权限
            // adb push * /sdcard/.
            // cp /sdcard/*.so .
            // chmod 644 *.so
        }
        val directorySoSource =
            DirectorySoSource(outputDir, SoSource.LOAD_FLAG_ALLOW_IMPLICIT_PROVISION)
        SoLoader.prependSoSource(directorySoSource)

        complete()
    }

    private fun getDir(context: Context): File {
        return context.getDir(NATIVE_DIR, Context.MODE_PRIVATE)
    }

    fun load(libName: String): Boolean {
        val extractDir: File = getDir(sContext)
        if (extractDir.exists() && extractDir.isDirectory && extractDir.canRead()) {
            val targetPackedLib = File(extractDir, System.mapLibraryName(libName))
            if (targetPackedLib.exists() && targetPackedLib.canRead()) {
                return SoLoader.loadLibrary(libName)
            }
        }

        return false
    }
}