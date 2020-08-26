package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator

class GameManager(
        private val pieceTextures: Map<Piece, Texture>,
        greenDot: Texture,
        redDot: Texture,
        boardTexture: Texture,
        private val onPromote: (callback: (chosenPiece: Piece) -> Unit) -> Unit
): Widget() {

    object Size {
        val BOARD_WIDTH = 1200
    }

    val gameEnded = false
    val isPlayingWhites = true

    private val boardActor = BoardActor(boardTexture)
    private val boardState = Board().apply {
        isEnableEvents = true
        addEventListener(BoardEventType.ON_MOVE) { onMove(it as Move) }
        addEventListener(BoardEventType.ON_UNDO_MOVE) {
            it as MoveBackup
            onMove(Move(it.move.to, it.move.from))
        }
    }

    private val indexedDots = mutableMapOf<Square, GreenDotActor>()
    private val greenDotGroup = Group().apply {
        Square.values().filter {
            it != Square.NONE
        }.map {
            it to GreenDotActor(isPlayingWhites, this@GameManager::onTapDot, greenDot)
        }.forEach { (square, actor) ->
            addActor(actor)
            actor.square = square
            indexedDots[square] = actor
            actor.isVisible = false
        }
    }

    private val redDotActor = RedDotActor(isPlayingWhites, redDot)
    private val redDotGroup = Group().apply {
        addActor(redDotActor)
        redDotActor.touchable = Touchable.disabled
        redDotActor.isVisible = false
    }

    private var selected: Square? = null

    private val indexedPieces = mutableMapOf<Square, PieceActor>()
    private val pieceGroup = Group().apply {
        Square.values().map {
            it to boardState.getPiece(it)
        }.filter { (_, piece) ->
            piece != Piece.NONE
        }.map { (square, piece) ->
            square to PieceActor(pieceTextures.getValue(piece), isPlayingWhites, this@GameManager::onTapPiece)
        }.forEach { (square, actor) ->
            addActor(actor)
            actor.square = square
            indexedPieces[square] = actor
        }
    }

    private fun onTapPiece(square: Square) {
        if (selected == square) {
            selected = null
            greenDotGroup.children.map { it.isVisible = false }
            return
        }

        selected = square

        showMoves(square)
    }

    private fun onTapDot(square: Square, isPromoting: Boolean) {
        if (selected != null) {
            if(isPromoting) {
                onPromote() { chosenPiece: Piece ->
                    val move = Move(selected, square, chosenPiece)

                    if (indexedDots[square]?.isVisible == true) {
                        unselect()
                        boardState.doMove(move)
                    }
                }
            } else {
                val move = Move(selected, square)

                if (indexedDots[square]?.isVisible == true) {
                    unselect()
                    boardState.doMove(move)
                }
            }
        }
    }

    private fun onMove(move: Move) {
        indexedPieces[move.from]!!.square = move.to

        if (indexedPieces[move.to] != null) {
            indexedPieces[move.to]!!.isVisible = false
        }

        indexedPieces[move.to] = indexedPieces[move.from]!!
        indexedPieces.remove(move.from)

        if(move.promotion != null && move.promotion != Piece.NONE) {
            indexedPieces[move.to]!!.isVisible = false
            pieceGroup.removeActor(indexedPieces[move.to]!!)

            PieceActor(
                    pieceTextures[move.promotion]!!,
                    isPlayingWhites,
                    this::onTapPiece
            ).apply {
                indexedPieces[move.to] = this
                pieceGroup.addActor(this)
                this.square = move.to
            }
        }

        val castlings = listOf(Constants.DEFAULT_WHITE_OO, Constants.DEFAULT_WHITE_OOO, Constants.DEFAULT_BLACK_OO, Constants.DEFAULT_BLACK_OOO)
        val isCastling = castlings.contains(move) && boardState.getPiece(move.to).pieceType == PieceType.KING

        if(isCastling) {
            val rookMove = when(move) {
                Constants.DEFAULT_WHITE_OO -> Constants.DEFAULT_WHITE_ROOK_OO
                Constants.DEFAULT_WHITE_OOO -> Constants.DEFAULT_WHITE_ROOK_OOO
                Constants.DEFAULT_BLACK_OO -> Constants.DEFAULT_BLACK_ROOK_OO
                Constants.DEFAULT_BLACK_OOO -> Constants.DEFAULT_BLACK_ROOK_OOO
                else -> null
            }
            onMove(rookMove!!)
        }

        //Hack for en passant case
        pieceGroup.children.map {
            it as PieceActor
        }.filter {
            val piece = boardState.getPiece(it.square)
            piece == null || piece == Piece.NONE
        }.forEach {
            it.isVisible = false
        }

        if (boardState.isKingAttacked) {
            redDotActor.isVisible = true
            redDotActor.square = boardState.getKingSquare(boardState.sideToMove)
        } else {
            redDotActor.isVisible = false
        }
    }

    private fun unselect() {
        selected = null
        greenDotGroup.children.map {
            it as GreenDotActor
        }.forEach {
            it.isPromoting = false
            it.isVisible = false
        }
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        stage?.addActor(boardActor)
        stage?.addActor(pieceGroup)
        stage?.addActor(greenDotGroup)
        stage?.addActor(redDotGroup)
    }

    override fun layout() {
        boardActor.width = width
        boardActor.height = height
        boardActor.setPosition(x, y)

        pieceGroup.setPosition(x, y)

        greenDotGroup.setPosition(x, y)

        redDotGroup.setPosition(x, y)
    }

    override fun getPrefWidth(): Float {
        return GameManager.Size.BOARD_WIDTH.toFloat()
    }

    override fun getPrefHeight(): Float {
        return GameManager.Size.BOARD_WIDTH.toFloat()
    }

    private fun showMoves(square: Square) {
        indexedDots.values.forEach {
            it.isVisible = false
        }
        MoveGenerator
                .generateLegalMoves(boardState)
                .asSequence()
                .filter { it.from == square }
                .map { (it.promotion != null && it.promotion != Piece.NONE) to it  }
                .map { (isPromotion, move) -> isPromotion to move.to }
                .forEach { (isPromotion, objective) ->
                    indexedDots[objective]!!.isVisible = true
                    indexedDots[objective]!!.isPromoting = isPromotion
                }
    }
}