package xyz.yorek.performance.tools

object AnrLogPrinter {
    init {
        System.loadLibrary("memory")
    }

    external fun print()
}