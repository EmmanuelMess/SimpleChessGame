package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.bhlangonijr.chesslib.Square

class PieceActor(
        texture: Texture,
        isPlayingWhites: Boolean,
        onTap: (Square) -> Unit
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