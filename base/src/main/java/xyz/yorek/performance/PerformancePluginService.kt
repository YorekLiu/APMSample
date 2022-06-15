package xyz.yorek.performance

import android.content.Context
import xyz.yorek.performance.base.AbsPerformancePlugin

object PerformancePluginService {

    private val sPluginClassSet = mutableSetOf<Class<out AbsPerformancePlugin>>()
    private val sPluginInstanceMap = mutableMapOf<String, AbsPerformancePlugin>()
    private var sInitialized = false

    fun registerPlugin(clazz: Class<out AbsPerformancePlugin>) {
        sPluginClassSet.add(clazz)
    }

    fun init(context: Context) {
        if (sInitialized) return
        sInitialized = true

        sPluginClassSet.forEach { clazz ->
            val constructor = clazz.getConstructor(Context::class.java)
            val plugin = constructor.newInstance(context)
            plugin.init()

            sPluginInstanceMap[clazz.name] = plugin
        }
    }

    fun start() {
        sPluginInstanceMap.values.forEach { it.start() }
    }

    fun stop() {
        sPluginInstanceMap.values.forEach { it.stop() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : AbsPerformancePlugin> find(clazz: Class<T>): T? {
        val candidate = sPluginClassSet.find { it.isAssignableFrom(clazz) } ?: return null

        return sPluginInstanceMap[candidate.name] as T?
    }
}