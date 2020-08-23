package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.emmanuelmess.simplechess.Colors.*
import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator


class GameBoard(
        boardWidth: Int,
        private val pieceTextures: Map<Piece, Pixmap>
): Image(), Disposable {

    val gameEnded = false
    val isPlayingWhites = true

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
    private val greenDots = mutableSetOf<Square>()
    private var selected: Square? = null

    init {
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val file = (x / squareSideSize).toInt()
                val rank = (y / squareSideSize).toInt()

                val square = Square.squareAt(file + 8 * rank)

                if(selected != null) {
                    val move = Move(selected, square)

                    if (greenDots.contains(square)) {
                        selected = null
                        greenDots.clear()
                        boardState.doMove(move)
                        return true
                    }
                }

                if(boardState.getPiece(square) != Piece.NONE) {
                    selected = square

                    showMoves(square)
                }

                return true
            }
        })
    }

    override fun layout() {
        drawBoard()
        drawPieces()
        drawGreenDots()
        drawCheckIndicator()
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
        greenDots.clear()
        greenDots.addAll(
                MoveGenerator
                        .generateLegalMoves(boardState)
                        .filter { it.from == square }
                        .map { it.to }
        )

        invalidate()
    }
    
    private fun fillSquare(x: Int, y: Int) {
        pixmap.fillRectangle(x * squareSideSize, y * squareSideSize, squareSideSize, squareSideSize)
    }
    
    private fun drawPiece(piece: Piece, square: Square) {
        if(piece == Piece.NONE) return

        val x = square.file.ordinal
        val y = convertRank(square.rank)

        pixmap.drawPixmap(
                pieceTextures[piece],
                0,
                0,
                pieceTextures[piece]!!.width,
                pieceTextures[piece]!!.height,
                squareSideSize * x,
                squareSideSize * y,
                squareSideSize,
                squareSideSize
        )
    }

    private fun drawGreenDots() {
        greenDots.forEach(this::drawGreenDot)
    }

    private fun drawGreenDot(square: Square) {
        drawDot(square, GREEN_COLOR)
    }

    private fun drawCheckIndicator() {
        if(boardState.isKingAttacked) {
            drawDot(boardState.getKingSquare(boardState.sideToMove), Color.RED)
        }
    }

    private fun drawDot(square: Square, color: Color) {
        val x = square.file.ordinal
        val y = convertRank(square.rank)
        pixmap.setColor(color)
        pixmap.fillCircle(x * squareSideSize + squareSideSize / 2,
                y * squareSideSize + squareSideSize / 2, squareSideSize / 5)
    }

    override fun dispose() {
        pixmap.dispose()
    }

    private fun convertRank(rank: Rank) = if(isPlayingWhites) (-rank.ordinal + 7) else rank.ordinal

}