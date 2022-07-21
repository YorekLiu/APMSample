package xyz.yorek.performance.apksize

import android.util.Log
import xyz.yorek.plugin.inst.anno.MethodProxy
import xyz.yorek.plugin.inst.anno.MethodProxyEntry

@MethodProxyEntry
object ApkSizeInst {

    private const val TAG = "ApkSize.Inst"

    @JvmStatic
    @MethodProxy(clazz = [System::class], method = "loadLibrary")
    fun loadLibrary(libName: String) {
        Log.d(TAG, "loadLibrary $libName")
        if (SoDynamicLoader.SO_MODE == SoDynamicLoader.Mode.NONE) {
            System.loadLibrary(libName)
        } else {
            SoDynamicLoader.load(libName)
        }
    }
}