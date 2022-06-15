package xyz.yorek.performance.memory.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import xyz.yorek.plugin.inst.anno.MethodProxyEntry
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * 添加@MethodProxyEntry注释，防止插件自动修改此类里面的内容
 */
@MethodProxyEntry
object CachedBitmapFactory {
    private val sCache = ConcurrentHashMap<Int, WeakReference<Bitmap?>>()

    fun decodeResources(resources: Resources, id: Int): Bitmap? {
        val candidate = sCache[id]?.get()
        return if (candidate == null) {
            val bitmap = BitmapFactory.decodeResource(resources, id)
            sCache[id] = WeakReference(bitmap)
            bitmap
        } else {
            candidate
        }
    }
}