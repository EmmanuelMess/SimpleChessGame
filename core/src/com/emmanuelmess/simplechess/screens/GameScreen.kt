package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.emmanuelmess.simplechess.Colors
import com.emmanuelmess.simplechess.GlobalData
import com.emmanuelmess.simplechess.Screen
import com.emmanuelmess.simplechess.game.*
import com.emmanuelmess.simplechess.game.GameManager.Size.BOARD_WIDTH
import com.emmanuelmess.simplechess.listener
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.*
import com.github.bhlangonijr.chesslib.PieceType
import java.util.concurrent.TimeUnit.MINUTES


class GameScreen(
        private val globalData: GlobalData,
        private val gameType: GameType
) : Screen(globalData) {
    private lateinit var assetManager: AssetManager
    private lateinit var stage: Stage
    private lateinit var promotingSelection: Table
    private lateinit var gameBoard: GameManager
    private var promotionCallback: ((chosenPiece: Piece) -> Unit)? = null
    private lateinit var pieceTextures: Map<Piece, Texture>
    private lateinit var greenDotTexture: Texture
    private lateinit var redDotTexture: Texture
    private lateinit var boardTexture: Texture
    private lateinit var popupPieceTextures: Map<Piece, SpriteDrawable>
    private lateinit var playerTime: TimeLabel
    private lateinit var opponentTime: TimeLabel
    private lateinit var timeKeeper: TimeKeeper

    override fun create() {
        assetManager = AssetManager().apply {
            load("images/pieces/bB.png", Texture::class.java)
            load("images/pieces/bK.png", Texture::class.java)
            load("images/pieces/bN.png", Texture::class.java)
            load("images/pieces/bP.png", Texture::class.java)
            load("images/pieces/bQ.png", Texture::class.java)
            load("images/pieces/bR.png", Texture::class.java)
            load("images/pieces/wB.png", Texture::class.java)
            load("images/pieces/wK.png", Texture::class.java)
            load("images/pieces/wN.png", Texture::class.java)
            load("images/pieces/wP.png", Texture::class.java)
            load("images/pieces/wQ.png", Texture::class.java)
            load("images/pieces/wR.png", Texture::class.java)

            load("sound/Capture.ogg", Sound::class.java)
            load("sound/Check.ogg", Sound::class.java)
            load("sound/Defeat.ogg", Sound::class.java)
            load("sound/Draw.ogg", Sound::class.java)
            load("sound/Move.ogg", Sound::class.java)
            load("sound/Victory.ogg", Sound::class.java)

            finishLoading()
        }

        pieceTextures = mapOf(
                BLACK_PAWN to assetManager["images/pieces/bP.png"],
                BLACK_ROOK to assetManager["images/pieces/bR.png"],
                BLACK_BISHOP to assetManager["images/pieces/bB.png"],
                BLACK_KNIGHT to assetManager["images/pieces/bN.png"],
                BLACK_QUEEN to assetManager["images/pieces/bQ.png"],
                BLACK_KING to assetManager["images/pieces/bK.png"],

                WHITE_PAWN to assetManager["images/pieces/wP.png"],
                WHITE_ROOK to assetManager["images/pieces/wR.png"],
                WHITE_BISHOP to assetManager["images/pieces/wB.png"],
                WHITE_KNIGHT to assetManager["images/pieces/wN.png"],
                WHITE_QUEEN to assetManager["images/pieces/wQ.png"],
                WHITE_KING to assetManager["images/pieces/wK.png"]
        )
        Pixmap(SquareActor.Size.SQUARE_WIDTH, SquareActor.Size.SQUARE_WIDTH, Pixmap.Format.RGBA8888).also {
            it.setColor(Colors.GREEN_COLOR)
            it.fillCircle(SquareActor.Size.SQUARE_WIDTH / 2, SquareActor.Size.SQUARE_WIDTH / 2, SquareActor.Size.SQUARE_WIDTH / 5)
            greenDotTexture = Texture(it)
        }
        Pixmap(SquareActor.Size.SQUARE_WIDTH, SquareActor.Size.SQUARE_WIDTH, Pixmap.Format.RGBA8888).also {
            it.setColor(Color.RED)
            it.fillCircle(SquareActor.Size.SQUARE_WIDTH / 2, SquareActor.Size.SQUARE_WIDTH / 2, SquareActor.Size.SQUARE_WIDTH / 5)
            redDotTexture = Texture(it)
        }
        Pixmap(BOARD_WIDTH, BOARD_WIDTH, Pixmap.Format.RGBA8888).also {
            drawBoard(it)
            boardTexture = Texture(it)
        }
        popupPieceTextures = pieceTextures
                .filter {
                    setOf(PieceType.BISHOP, PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK).contains(it.key.pieceType)
                }.map { (piece, texture) ->
                    piece to Sprite(texture).apply {
                        setSize((BOARD_WIDTH / 4).toFloat(), (BOARD_WIDTH / 4).toFloat())
                    }
                }.map { (piece, sprite) ->
                    piece to SpriteDrawable(sprite)
                }.toMap()

        stage = Stage(globalData.textViewport)

        stage.addActor(Container(Image(globalData.lichessIcon)).apply {
            setFillParent(true)
        })

        gameBoard = GameManager(
                pieceTextures,
                greenDotTexture,
                redDotTexture,
                boardTexture,
                assetManager["sound/Capture.ogg"],
                assetManager["sound/Check.ogg"],
                assetManager["sound/Defeat.ogg"],
                assetManager["sound/Draw.ogg"],
                assetManager["sound/Move.ogg"],
                assetManager["sound/Victory.ogg"],
                { callback: (chosenPiece: Piece) -> Unit ->
                    promotionCallback = callback
                    promotingSelection.isVisible = true
                },
                { isPlayer ->
                    if (isPlayer) {
                        playerTime.stop(TimeKeeper.getTime())
                        opponentTime.start(TimeKeeper.getTime())
                    } else {
                        playerTime.start(TimeKeeper.getTime())
                        opponentTime.stop(TimeKeeper.getTime())
                    }
                },
                this::onGameEnded
        )

        playerTime = TimeLabel(
                MINUTES.toSeconds(gameType.time.toLong()),
                gameType.timeAdded,
                globalData.skin,
                { onTimerEnded(true) }
        )
        opponentTime = TimeLabel(
                MINUTES.toSeconds(gameType.time.toLong()),
                gameType.timeAdded,
                globalData.skin,
                { onTimerEnded(false) }
        )

        val table = Table(globalData.skin).apply {
            add(Label(globalData.translate[gameType.category.readableName], globalData.skin, "default120", Color.BLACK))
                    .colspan(3).left().top()
            row().padTop(100f)
            add(opponentTime).left()
            add()
            add(playerTime).right()
            row()
            add(gameBoard).colspan(3).center()
            row()
            add(TextButton("undo", skin).apply {
                listener {
                    gameBoard.undo()
                }
            })
            add(TextButton("draw", skin).apply {
                listener {
                    gameBoard.gameEnded = GameEndState.STALEMATE
                }
            })
            add(TextButton("surrender", skin).apply {
                listener {
                    gameBoard.gameEnded = GameEndState.LOST
                }
            })
            setFillParent(true)
        }

        stage.addActor(table)

        promotingSelection = Table(globalData.skin).apply {
            add(Label(globalData.translate["choose_piece"], skin, "default120", Color.BLACK)).colspan(3).left().top()
            row().padTop(100f)

            val pieces =
                    if(gameBoard.isPlayingWhites) listOf(WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN)
                    else listOf(BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN)
            pieces.forEach {
                add(ImageButton(popupPieceTextures[it]).apply {
                    image.width = (BOARD_WIDTH / 4).toFloat()
                    listener {
                        promotingSelection.isVisible = false
                        promotionCallback?.invoke(it)
                    }
                })
            }
            setFillParent(true)
            isVisible = false
        }

        stage.addActor(promotingSelection)

        val backProcessor: InputProcessor = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.BACK) {
                    globalData.changeScreen(GameTypeSelectScreen(globalData))
                    return true
                }
                return false
            }
        }

        Gdx.input.inputProcessor = InputMultiplexer(stage, backProcessor)
        Gdx.input.setCatchKey(Input.Keys.BACK, true)

        timeKeeper = TimeKeeper(this::onTick)
    }

    private fun onTick(tick: Long) {
        opponentTime.update(tick)
        playerTime.update(tick)
        Gdx.graphics.requestRendering();
    }

    private fun onTimerEnded(isPlayerTimer: Boolean) {
        gameBoard.gameEnded = if(isPlayerTimer) GameEndState.LOST else GameEndState.WON
    }

    private fun onGameEnded(gameEndState: GameEndState) {
        playerTime.stop(TimeKeeper.getTime())
        opponentTime.stop(TimeKeeper.getTime())

        val rawText = when(gameEndState) {
            GameEndState.WON -> "won"
            GameEndState.LOST -> "lost"
            GameEndState.STALEMATE -> "stalemate"
        }

        val gameEndedTable = Table(globalData.skin).apply {
            add(Label(globalData.translate[rawText], skin, "boldbig", Color.RED)).center()
            setFillParent(true)
        }

        stage.addActor(gameEndedTable)
    }

    private fun drawBoard(pixmap: Pixmap) {
        pixmap.setColor(Colors.LIGHT_COLOR)
        pixmap.fillRectangle(0, 0, pixmap.width, pixmap.height)

        pixmap.setColor(Colors.DARK_COLOR)

        val doOddRow: (y: Int) -> Unit = { y: Int ->
            fillSquare(pixmap, 1, y)
            fillSquare(pixmap, 3, y)
            fillSquare(pixmap, 5, y)
            fillSquare(pixmap, 7, y)
        }

        val doEvenRow: (y: Int) -> Unit = { y: Int ->
            fillSquare(pixmap, 0, y)
            fillSquare(pixmap, 2, y)
            fillSquare(pixmap, 4, y)
            fillSquare(pixmap, 6, y)
            fillSquare(pixmap, 8, y)
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

    private fun fillSquare(pixmap: Pixmap, x: Int, y: Int) {
        pixmap.fillRectangle(x * SquareActor.Size.SQUARE_WIDTH, y * SquareActor.Size.SQUARE_WIDTH, SquareActor.Size.SQUARE_WIDTH, SquareActor.Size.SQUARE_WIDTH)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render() {
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.setCatchKey(Input.Keys.BACK, false)
        stage.dispose()
        assetManager.dispose()
        timeKeeper.kill()
    }
}