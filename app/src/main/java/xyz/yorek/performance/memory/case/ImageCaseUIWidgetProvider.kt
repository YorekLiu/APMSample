package xyz.yorek.performance.memory.case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import xyz.yorek.performance.R
import xyz.yorek.performance.base.CaseUIWidgetProvider
import xyz.yorek.performance.databinding.ViewManageImageBinding
import xyz.yorek.performance.memory.bitmap.AppBitmapMonitor

class ImageCaseUIWidgetProvider : CaseUIWidgetProvider() {
    private lateinit var mViewBinding: ViewManageImageBinding

    override fun getTitle(): String = "Bitmap管理"

    override fun onCreate(context: Context): View {
        mViewBinding = ViewManageImageBinding.inflate(LayoutInflater.from(context))
        return mViewBinding.root
    }

    override fun onInit() {
        mViewBinding.imageViewDecode1.setImageBitmap(decodeBitmap())
        mViewBinding.imageViewDecode2.setImageBitmap(decodeBitmap())

        mViewBinding.textIsSameFromSrc.text = if (isBitmapSame(mViewBinding.imageViewSrc1, mViewBinding.imageViewSrc2)) "使用src加载图片时：图片地址相同" else "使用src加载图片时：图片地址不同"
        mViewBinding.textIsSameFromDecode.text = if (isBitmapSame(mViewBinding.imageViewDecode1, mViewBinding.imageViewDecode2)) "使用BitmapFactory加载图片时，图片地址本应该不同：地址相同，被统一收拢了" else "使用BitmapFactory加载图片时，图片地址本应该不同：地址不同，Hook失效了？"

        mViewBinding.imageViewLarge1.setImageResource(R.drawable.img_large)
        Glide.with(mContext)
            .load(R.drawable.img_large)
            .into(mViewBinding.imageViewLarge2)

        mViewBinding.btnPrintAliveBitmap.setOnClickListener {
            AppBitmapMonitor.getAllAliveBitmap().forEach { bitmap ->
                Log.d(TAG, "alive bitmap: hashCode=${bitmap.hashCode()}, size=${bitmap.width}-${bitmap.height}")
            }
        }
    }

    private fun decodeBitmap(): Bitmap {
        return BitmapFactory.decodeResource(mContext.resources, R.drawable.ic_retry)
    }

    private fun isBitmapSame(a: ImageView, b: ImageView): Boolean {
        if (a.drawable is BitmapDrawable && b.drawable is BitmapDrawable) {
            return System.identityHashCode((a.drawable as BitmapDrawable).bitmap) == System.identityHashCode((b.drawable as BitmapDrawable).bitmap)
        }
        return false
    }
}