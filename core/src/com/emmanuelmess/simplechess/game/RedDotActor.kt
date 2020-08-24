package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.github.bhlangonijr.chesslib.Square

class RedDotActor(
        isPlayingWhites: Boolean,
        texture: Texture
): SquareActor(isPlayingWhites, texture)