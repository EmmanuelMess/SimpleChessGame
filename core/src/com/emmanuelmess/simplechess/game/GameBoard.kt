package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.emmanuelmess.simplechess.Colors.*
import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.move.MoveGenerator

class GameBoard(
        boardWidth: Int,
        private val pieceTextures: Map<Piece, Pixmap>
): Image(), Disposable {

    val gameEnded = false

    private val pixmap = Pixmap(boardWidth, boardWidth, Pixmap.Format.RGBA8888)
    private val texture = Texture(boardWidth, boardWidth, Pixmap.Format.RGBA8888)
    private val squareSideSize = boardWidth/8
    private val boardState = Board().apply {
        val inval: (BoardEvent) -> Unit = { invalidate() }

        isEnableEvents = true
        addEventListener(BoardEventType.ON_MOVE, inval)
        addEventListener(BoardEventType.ON_UNDO_MOVE, inval)
        addEventListener(BoardEventType.ON_LOAD, inval)
    }

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
        for (square in Square.values()) {
            drawPiece(boardState.getPiece(square), square)
        }
    }

    private fun showMoves(square: Square) {
        MoveGenerator.generateLegalMoves(boardState)
                .filter { it.from == square }
                .map { it.to }
                .forEach(this::drawGreenDot)
    }
    
    private fun fillSquare(x: Int, y: Int) {
        pixmap.fillRectangle(x* squareSideSize, y * squareSideSize, squareSideSize, squareSideSize)
    }
    
    private fun drawPiece(piece: Piece, square: Square) {
        if(piece == Piece.NONE) return

        val x = square.file.ordinal
        val y = square.rank.ordinal

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

    private fun drawGreenDot(square: Square) {
        val x = square.file.ordinal
        val y = square.rank.ordinal
        pixmap.setColor(GREEN_COLOR)
        pixmap.fillCircle(x * squareSideSize + squareSideSize /2,
                y * squareSideSize + squareSideSize /2, squareSideSize / 5)
    }

    override fun dispose() {
        pixmap.dispose()
    }

}