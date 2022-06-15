package xyz.yorek.performance.memory.device

import android.content.Context
import com.facebook.device.yearclass.YearClass

object DevicePerformance {
    enum class DeviceYear {
        LOW,
        MEDIUM,
        HIGH
    }

    fun getDevicePerformance(context: Context): DeviceYear {
        val yearClass = YearClass.get(context)
        return when {
            yearClass >= 2013 -> DeviceYear.HIGH
            yearClass > 2010 -> DeviceYear.MEDIUM
            else -> DeviceYear.LOW
        }
    }
}