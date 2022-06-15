package xyz.yorek.performance.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class ViewBindingBaseActivity<T : ViewBinding> : BaseActivity() {

    protected lateinit var mBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getViewBindingInstance()

        setContentView(mBinding.root)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewBindingInstance(): T {
        val viewBindingType = (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        val viewBindingInflateMethod = viewBindingType.getDeclaredMethod("inflate", LayoutInflater::class.java)

        return viewBindingInflateMethod.invoke(null, layoutInflater) as T
    }
}