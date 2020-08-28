package com.emmanuelmess.simplechess.game

import com.badlogic.gdx.audio.Sound
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
        private val captureSound: Sound,
        private val checkSound: Sound,
        private val defeatSound: Sound,
        private val drawSound: Sound,
        private val moveSound: Sound,
        private val victorySound: Sound,
        private val onPromote: (callback: (chosenPiece: Piece) -> Unit) -> Unit,
        private val onMoveFinished: (isPlayer: Boolean) -> Unit,
        private val onGameFinished: (GameEndState) -> Unit
): Widget() {

    object Size {
        val BOARD_WIDTH = 1200
    }

    val isPlayingWhites = true

    var gameEnded: GameEndState? = null
        set(value) {
            if(value == null) throw NullPointerException()
            if(field != null) return

            when(value) {
                GameEndState.WON -> victorySound.play()
                GameEndState.LOST -> defeatSound.play()
                GameEndState.STALEMATE -> drawSound.play()
            }

            selected = null
            greenDotGroup.children.map { it.isVisible = false }
            field = value
            onGameFinished(value)
        }

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

    public fun undo() {
        TODO()
    }

    private fun onTapPiece(square: Square) {
        if(gameEnded != null) {
            return
        }

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
        var soundHasPlayed = false

        indexedPieces[move.from]!!.square = move.to

        if (indexedPieces[move.to] != null) {
            indexedPieces[move.to]!!.isVisible = false
            captureSound.play()
            soundHasPlayed = true
        }

        indexedPieces[move.to] = indexedPieces[move.from]!!
        indexedPieces.remove(move.from)

        if(move.promotion != null && move.promotion != Piece.NONE) {
            indexedPieces[move.to]!!.isVisible = false

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
            captureSound.play()
            soundHasPlayed = true
        }

        if (boardState.isKingAttacked) {
            redDotActor.isVisible = true
            redDotActor.square = boardState.getKingSquare(boardState.sideToMove)
            checkSound.play()
            soundHasPlayed = true
        } else {
            redDotActor.isVisible = false
        }

        pieceGroup.children.filter { !it.isVisible }.forEach { pieceGroup.removeActor(it) }

        onMoveFinished(
                (boardState.getPiece(move.to).pieceSide == Side.WHITE && isPlayingWhites)
                        || (boardState.getPiece(move.to).pieceSide == Side.BLACK && !isPlayingWhites)
        )

        if(boardState.isDraw) {
            gameEnded = GameEndState.STALEMATE
            soundHasPlayed = true
        } else if(boardState.isMated) {
            val won = boardState.sideToMove
            gameEnded = if((won == Side.WHITE && isPlayingWhites) || (won == Side.BLACK && !isPlayingWhites)) {
                GameEndState.LOST
            } else {
                GameEndState.WON
            }
            soundHasPlayed = true
        }

        if(!soundHasPlayed) {
            moveSound.play()
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