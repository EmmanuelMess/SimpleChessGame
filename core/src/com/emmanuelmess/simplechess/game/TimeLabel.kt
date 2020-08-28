package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS

class TimeLabel(
        initialTime: Long,
        private val sumTime: Long,
        skin: Skin,
        private val onNoTimeCallback: () -> Unit
): Label(convert(initialTime), skin) {
    companion object {
        @JvmStatic
        private fun convert(seconds: Long) =
            String.format("%02d:%02d",
                    SECONDS.toMinutes(seconds),
                    SECONDS.toSeconds(seconds) - MINUTES.toSeconds(SECONDS.toMinutes(seconds))
            )
    }

    private var currentTime: Long = initialTime
        set(value) {
            setText(convert(value))
            field = value
        }

    private var startTime: Long? = null

    fun isRunning() = startTime != null

    fun start(time: Long) {
        startTime = time
    }

    fun update(time: Long) {
        startTime?.let {
            val elapsed = time - it
            setText(convert(currentTime - elapsed))

            if(currentTime - elapsed < 0) {
                onNoTimeCallback()
                currentTime = 0
                startTime = null
                setText(convert(0))
            }
        }
    }

    fun stop(time: Long) {
        startTime?.let {
            val elapsed = time - it
            if(elapsed < sumTime) {
                currentTime += (sumTime - elapsed)
            } else {
                currentTime -= elapsed
            }
        }
        startTime = null
    }
}