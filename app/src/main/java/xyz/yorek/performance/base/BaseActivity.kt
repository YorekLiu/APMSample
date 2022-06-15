package xyz.yorek.performance.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected val TAG = javaClass.simpleName
}