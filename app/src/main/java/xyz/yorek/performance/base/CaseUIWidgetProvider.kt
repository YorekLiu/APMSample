package xyz.yorek.performance.base

import android.content.Context
import android.view.View

abstract class CaseUIWidgetProvider {
    protected val TAG = javaClass.simpleName

    protected lateinit var mContext: Context
    protected lateinit var mView: View

    abstract fun getTitle(): String

    fun create(context: Context): View {
        mContext = context
        mView = onCreate(context)
        return mView
    }

    abstract fun onCreate(context: Context): View

    abstract fun onInit()

    open fun onDestroy() {}
}