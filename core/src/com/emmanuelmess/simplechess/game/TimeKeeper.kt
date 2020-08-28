package com.emmanuelmess.simplechess.game

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class TimeKeeper(
        val callback: (timeElapsedSecs: Long) -> Unit
) {
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1).apply {
        val runnable = Runnable {
            callback(getTime())
        }

        scheduleAtFixedRate(runnable, 0, 500, TimeUnit.MILLISECONDS)
    }

    fun kill() {
        executor.shutdownNow()
    }

    companion object {
        @JvmStatic
        fun getTime(): Long = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime())
    }
}