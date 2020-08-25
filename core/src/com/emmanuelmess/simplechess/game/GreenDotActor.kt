package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.github.bhlangonijr.chesslib.Square

class GreenDotActor(
        isPlayingWhites: Boolean,
        onTap: (Square, Boolean, Boolean) -> Unit,
        texture: Texture
): SquareActor(isPlayingWhites, texture) {
    public var isPromoting: Boolean = false
    public var isCastling: Boolean = false

    init {
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                onTap(square, isPromoting, isCastling)
                return true
            }
        })
    }
}