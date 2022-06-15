package xyz.yorek.performance.memory

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.Keep
import xyz.yorek.performance.memory.bitmap.AppBitmapMonitor
import xyz.yorek.performance.memory.bitmap.CachedBitmapFactory
import xyz.yorek.plugin.inst.anno.MethodProxy
import xyz.yorek.plugin.inst.anno.MethodProxyEntry
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Keep
@MethodProxyEntry
object MemoryMethodInst {
    private const val TAG = "MemoryMethodInst"
    private lateinit var sExecutorService: ExecutorService

    @JvmStatic
    @MethodProxy(clazz = [ Thread::class ], method = "start")
    fun hookThreadStart(thread: Thread) {
        val targetField = thread.javaClass.getDeclaredField("target")
        targetField.isAccessible = true
        val target = targetField.get(thread)
        if (target is Runnable) {
            submitRunnableToPool(target)
        }
    }

    private fun submitRunnableToPool(target: Runnable) {
        ensureExecutor()
        sExecutorService.submit(target)
    }

    @Synchronized
    private fun ensureExecutor() {
        if (!::sExecutorService.isInitialized) {
            sExecutorService = Executors.newCachedThreadPool()
        }
    }

    @JvmStatic
    @MethodProxy(clazz = [BitmapFactory::class], method = "decodeResource")
    fun decode(resources: Resources, id: Int): Bitmap? {
        return CachedBitmapFactory.decodeResources(resources, id)
    }

    @JvmStatic
    @MethodProxy(clazz = [ImageView::class], method = "setImageResource")
    fun setImageResource(imageView: ImageView, resId: Int) {
//        Log.d(TAG, "setImageResource called from: ${Throwable().stackTraceToString()}")
        imageView.setImageResource(resId)

        if (imageView.drawable is BitmapDrawable) {
            checkImageView(imageView, (imageView.drawable as BitmapDrawable).bitmap)
        }
    }

    @JvmStatic
    @MethodProxy(clazz = [ImageView::class], method = "setImageBitmap")
    fun setImageBitmap(imageView: ImageView, bitmap: Bitmap?) {
//        Log.d(TAG, "setImageBitmap called from: ${Throwable().stackTraceToString()}")
        imageView.setImageBitmap(bitmap)

        checkImageView(imageView, bitmap)
    }

    @JvmStatic
    @MethodProxy(clazz = [ImageView::class], method = "setImageDrawable")
    fun setImageDrawable(imageView: ImageView, drawable: Drawable?) {
//        Log.d(TAG, "setImageDrawable called from: ${Throwable().stackTraceToString()}")
        imageView.setImageDrawable(drawable)

        if (drawable is BitmapDrawable) {
            checkImageView(imageView, drawable.bitmap)
        }
    }

    private fun checkImageView(imageView: ImageView, bitmap: Bitmap?) {
        AppBitmapMonitor.onBitmapGenerated(bitmap)

        val checkImageRunnable = Runnable {
            val bitmapWidth = bitmap?.width ?: 0
            val bitmapHeight = bitmap?.height ?: 0
            val viewWidth = imageView.width
            val viewHeight = imageView.height
            Log.d(TAG, "bitmapSize=${bitmapWidth}-${bitmapHeight}, viewSize=${viewWidth}-${viewHeight}")

            if (bitmapWidth >= 2 * viewWidth && bitmapHeight >= 2 * viewHeight) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    imageView.foreground =  ColorDrawable(0x55FF0000)
                }
            }
        }
        if (imageView.width == 0 && imageView.height == 0) {
            imageView.post(checkImageRunnable)
        } else {
            checkImageRunnable.run()
        }
    }
}