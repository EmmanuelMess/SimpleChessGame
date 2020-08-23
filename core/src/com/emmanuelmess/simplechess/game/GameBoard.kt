package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.emmanuelmess.simplechess.Colors.DARK_COLOR
import com.emmanuelmess.simplechess.Colors.LIGHT_COLOR
import com.emmanuelmess.simplechess.game.Piece.*

class GameBoard(
        boardWidth: Int,
        private val pieceTextures: Map<Piece, Pixmap>
): Image(), Disposable {

    val pixmap = Pixmap(boardWidth, boardWidth, Pixmap.Format.RGBA8888)
    val squareSideSize = boardWidth/8
    val boardState = mapOf(
            (0 to 0) to WHITE_ROOK,
            (1 to 0) to WHITE_KNIGHT,
            (2 to 0) to WHITE_BISHOP,
            (3 to 0) to WHITE_KING,
            (4 to 0) to WHITE_QUEEN,
            (5 to 0) to WHITE_BISHOP,
            (6 to 0) to WHITE_KNIGHT,
            (7 to 0) to WHITE_ROOK,

            (0 to 1) to WHITE_PAWN,
            (1 to 1) to WHITE_PAWN,
            (2 to 1) to WHITE_PAWN,
            (3 to 1) to WHITE_PAWN,
            (4 to 1) to WHITE_PAWN,
            (5 to 1) to WHITE_PAWN,
            (6 to 1) to WHITE_PAWN,
            (7 to 1) to WHITE_PAWN,
            
            (0 to 6) to BLACK_PAWN,
            (1 to 6) to BLACK_PAWN,
            (2 to 6) to BLACK_PAWN,
            (3 to 6) to BLACK_PAWN,
            (4 to 6) to BLACK_PAWN,
            (5 to 6) to BLACK_PAWN,
            (6 to 6) to BLACK_PAWN,
            (7 to 6) to BLACK_PAWN,
            
            (0 to 7) to BLACK_ROOK,
            (1 to 7) to BLACK_KNIGHT,
            (2 to 7) to BLACK_BISHOP,
            (3 to 7) to BLACK_KING,
            (4 to 7) to BLACK_QUEEN,
            (5 to 7) to BLACK_BISHOP,
            (6 to 7) to BLACK_KNIGHT,
            (7 to 7) to BLACK_ROOK
    )

    init {
        drawBoard()
        drawPieces()
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

    private fun drawPieces() {
        for ((pos, piece) in boardState) {
            val (x, y) = pos
            pixmap.drawPiece(piece, x, y)
        }
    }
    
    private fun Pixmap.fillSquare(x: Int, y: Int) {
        fillRectangle(x* squareSideSize, y * squareSideSize, squareSideSize, squareSideSize)
    }
    
    private fun Pixmap.drawPiece(piece: Piece, x: Int, y: Int) {
        drawPixmap(
                pieceTextures[piece],
                0,
                0,
                pieceTextures[piece]!!.width,
                pieceTextures[piece]!!.height,
                squareSideSize*x,
                squareSideSize*y,
                squareSideSize,
                squareSideSize
        )
    }

    override fun dispose() {
        pixmap.dispose()
    }

}