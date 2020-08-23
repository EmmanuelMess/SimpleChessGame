package com.emmanuelmess.simplechess

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.emmanuelmess.simplechess.Colors.DARK_COLOR
import com.emmanuelmess.simplechess.Colors.LIGHT_COLOR

class GameBoard(
        boardWidth: Int
): Image(), Disposable {

    val pixmap = Pixmap(boardWidth, boardWidth, Pixmap.Format.RGBA8888)
    val squareSideSize = boardWidth/8

    init {
        drawBoard()
        drawable = TextureRegionDrawable(Texture(pixmap))
    }

    private fun drawBoard() {
        pixmap.setColor(LIGHT_COLOR)
        pixmap.fillRectangle(0, 0, pixmap.width, pixmap.height)

        pixmap.setColor(DARK_COLOR)

        val doOddRow: (y: Int) -> Unit = { y: Int ->
            pixmap.fillSquare(1, y)
            pixmap.fillSquare(3, y)
            pixmap.fillSquare(5, y)
            pixmap.fillSquare(7, y)
        }

        val doEvenRow: (y: Int) -> Unit = { y: Int ->
            pixmap.fillSquare(0, y)
            pixmap.fillSquare(2, y)
            pixmap.fillSquare(4, y)
            pixmap.fillSquare(6, y)
            pixmap.fillSquare(8, y)
        }

        doOddRow(0)
        doEvenRow(1)
        doOddRow(2)
        doEvenRow(3)
        doOddRow(4)
        doEvenRow(5)
        doOddRow(6)
        doEvenRow(7)
    }

    private fun Pixmap.fillSquare(x: Int, y: Int) {
        fillRectangle(x* squareSideSize, y * squareSideSize, squareSideSize, squareSideSize)
    }

    override fun dispose() {
        pixmap.dispose()
    }

}