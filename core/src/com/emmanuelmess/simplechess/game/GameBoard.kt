package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.emmanuelmess.simplechess.Colors.*
import com.emmanuelmess.simplechess.game.Piece.*

class GameBoard(
        boardWidth: Int,
        private val pieceTextures: Map<Piece, Pixmap>
): Image(), Disposable {

    val gameEnded = false

    private val pixmap = Pixmap(boardWidth, boardWidth, Pixmap.Format.RGBA8888)
    private val texture = Texture(boardWidth, boardWidth, Pixmap.Format.RGBA8888)
    private val squareSideSize = boardWidth/8
    private val boardState = mutableMapOf(
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

    override fun layout() {
        drawBoard()
        drawPieces()
        texture.draw(pixmap, 0, 0)
        drawable = TextureRegionDrawable(texture)

        super.layout()
    }

    private fun drawBoard() {
        pixmap.setColor(LIGHT_COLOR)
        pixmap.fillRectangle(0, 0, pixmap.width, pixmap.height)

        pixmap.setColor(DARK_COLOR)

        val doOddRow: (y: Int) -> Unit = { y: Int ->
            fillSquare(1, y)
            fillSquare(3, y)
            fillSquare(5, y)
            fillSquare(7, y)
        }

        val doEvenRow: (y: Int) -> Unit = { y: Int ->
            fillSquare(0, y)
            fillSquare(2, y)
            fillSquare(4, y)
            fillSquare(6, y)
            fillSquare(8, y)
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
            drawPiece(piece, x, y)
        }
    }

    private fun showMoves(positions: Set<Pair<Int, Int>>) {
        for((x, y) in positions) {
            drawGreenDot(x, y)
        }
    }

    private fun movePiece(piece: Piece, x: Int, y: Int) {
        boardState.values.remove(piece)
        boardState[x to y] = piece
        invalidate()
    }
    
    private fun fillSquare(x: Int, y: Int) {
        pixmap.fillRectangle(x* squareSideSize, y * squareSideSize, squareSideSize, squareSideSize)
    }
    
    private fun drawPiece(piece: Piece, x: Int, y: Int) {
        pixmap.drawPixmap(
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

    private fun drawGreenDot(x: Int, y: Int) {
        if(boardState[x to y] == null) {
            pixmap.setColor(GREEN_COLOR)
            pixmap.fillCircle(x* squareSideSize, y * squareSideSize, squareSideSize/4)
        }
    }

    override fun dispose() {
        pixmap.dispose()
    }

}