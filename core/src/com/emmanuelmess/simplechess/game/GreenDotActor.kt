package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.github.bhlangonijr.chesslib.Square

class GreenDotActor(
        isPlayingWhites: Boolean,
        onTap: (Square) -> Unit,
        texture: Texture
): SquareActor(isPlayingWhites, texture) {
    init {
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                onTap(square)
                return true
            }
        })
    }
}