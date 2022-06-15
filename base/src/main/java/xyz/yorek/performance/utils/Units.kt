package xyz.yorek.performance.utils

object Units {
    fun convertB2MB(bytes: Int): Int {
        return bytes / (1024 * 1024)
    }

    fun convertB2KB(bytes: Int): Int {
        return bytes / 1024
    }

    fun convertKB2B(bytes: Int): Int {
        return bytes / 1024
    }

    fun convertB2MB(bytes: Long): Int {
        return (bytes / (1024 * 1024)).toInt()
    }

    fun convertB2KB(bytes: Long): Int {
        return (bytes / 1024).toInt()
    }
}