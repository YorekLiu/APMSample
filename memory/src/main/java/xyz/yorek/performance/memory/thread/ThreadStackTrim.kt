package xyz.yorek.performance.memory.thread

object ThreadStackTrim {
    init {
        System.loadLibrary("memory")
    }

    external fun getStackSize(): Int

    external fun installHook()
}