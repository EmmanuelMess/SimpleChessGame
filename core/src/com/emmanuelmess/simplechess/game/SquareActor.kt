package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.emmanuelmess.simplechess.game.SquareActor.Size.SQUARE_WIDTH
import com.github.bhlangonijr.chesslib.Rank
import com.github.bhlangonijr.chesslib.Square

open class SquareActor(
        val isPlayingWhites: Boolean,
        texture: Texture
): Image(texture) {

    object Size {
        val SQUARE_WIDTH = GameManager.Size.BOARD_WIDTH/8
    }

    init {
        width = SQUARE_WIDTH.toFloat()
        height = SQUARE_WIDTH.toFloat()
    }

    public var square: Square = Square.A1
        set(value) {
            val x = value.file.ordinal.toFloat()
            val y = convertRank(value.rank).toFloat()

            setPosition(Size.SQUARE_WIDTH * x, Size.SQUARE_WIDTH * y)

            field = value
        }

    private fun convertRank(rank: Rank) = if(isPlayingWhites) rank.ordinal else (-rank.ordinal + 7)
}