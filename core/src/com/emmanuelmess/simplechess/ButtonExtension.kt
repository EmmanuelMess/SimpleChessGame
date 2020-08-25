package com.emmanuelmess.simplechess

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.emmanuelmess.simplechess.server.Connection

fun Button.listener(listener: () -> Unit) {
    addListener(object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) = listener()
    })
}