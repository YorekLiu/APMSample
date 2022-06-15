package xyz.yorek.performance.memory.bitmap

import android.graphics.Bitmap
import java.util.*

object AppBitmapMonitor {
    private val sBitmapHolder = Collections.synchronizedMap(WeakHashMap<Int, Bitmap>())

    fun onBitmapGenerated(bitmap: Bitmap?) {
        bitmap ?: return
        val hashCode = bitmap.hashCode()
        sBitmapHolder[hashCode] = bitmap
    }

    fun getAllAliveBitmap(): Collection<Bitmap> {
        return sBitmapHolder.values
    }
}