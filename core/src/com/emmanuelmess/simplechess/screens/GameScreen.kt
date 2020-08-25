package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.emmanuelmess.simplechess.*
import com.emmanuelmess.simplechess.game.BoardActor
import com.emmanuelmess.simplechess.game.GameManager
import com.emmanuelmess.simplechess.game.GameManager.Size.BOARD_WIDTH
import com.emmanuelmess.simplechess.game.GameType
import com.emmanuelmess.simplechess.game.SquareActor
import com.emmanuelmess.simplechess.server.Connection
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.*
import com.github.bhlangonijr.chesslib.PieceType

class GameScreen(
        private val globalData: GlobalData,
        private val gameType: GameType
) : Screen(globalData) {
    private lateinit var assetManager: AssetManager
    private lateinit var stage: Stage
    private lateinit var promotingSelection: Table
    private var promotionCallback: ((chosenPiece: Piece) -> Unit)? = null
    private lateinit var skin80: Skin
    private lateinit var skin120: Skin
    private lateinit var pieceTextures: Map<Piece, Texture>
    private lateinit var greenDotTexture: Texture
    private lateinit var redDotTexture: Texture
    private lateinit var boardTexture: Texture
    private lateinit var popupPieceTextures: Map<Piece, SpriteDrawable>

    override fun create() {
        assetManager = AssetManager().apply {
            InternalFileHandleResolver().also {
                setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(it))
                setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(it))
            }

            load("icon/lichess grey.png", Texture::class.java, TextureLoader.TextureParameter())

            load("roboto-120.ttf", BitmapFont::class.java, FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
                fontFileName = "fonts/roboto-light-latin.ttf"
                fontParameters.size = 120
            })

            load("roboto-80.ttf", BitmapFont::class.java, FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
                fontFileName = "fonts/roboto-light-latin.ttf"
                fontParameters.size = 80
            })

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

            finishLoading()
        }

        skin80 = Skin().apply {
            add("default", Label.LabelStyle(assetManager.get<BitmapFont>("roboto-80.ttf"), Color.BLACK))
            add("default", TextField.TextFieldStyle(
                    assetManager.get<BitmapFont>("roboto-80.ttf"),
                    Color.BLACK,
                    null,
                    null,
                    null
            ))
            add("default", TextButton.TextButtonStyle(
                    null,
                    null,
                    null,
                    assetManager.get<BitmapFont>("roboto-80.ttf")
            ).apply {
                fontColor = Color.BLACK
            })
        }

        skin120 = Skin().apply {
            add("default", Label.LabelStyle(assetManager.get<BitmapFont>("roboto-120.ttf"), Color.BLACK))
            add("default", TextField.TextFieldStyle(
                    assetManager.get<BitmapFont>("roboto-120.ttf"),
                    Color.BLACK,
                    null,
                    null,
                    null
            ))
            add("default", TextButton.TextButtonStyle(
                    null,
                    null,
                    null,
                    assetManager.get<BitmapFont>("roboto-120.ttf")
            ).apply {
                fontColor = Color.BLACK
            })
        }
        pieceTextures = mapOf(
                BLACK_PAWN to assetManager["images/pieces/bP.png"],
                BLACK_ROOK to  assetManager["images/pieces/bR.png"],
                BLACK_BISHOP to  assetManager["images/pieces/bB.png"],
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
                        setSize((BOARD_WIDTH/4).toFloat(), (BOARD_WIDTH/4).toFloat())
                    }
                }.map { (piece, sprite) ->
                    piece to SpriteDrawable(sprite)
                }.toMap()

        stage = Stage(globalData.textViewport)

        val img: Texture = assetManager["icon/lichess grey.png"]
        stage.addActor(Container(Image(img)).apply {
            setFillParent(true)
        })

        val gameBoard = GameManager(pieceTextures, greenDotTexture, redDotTexture, boardTexture) { callback: (chosenPiece: Piece) -> Unit ->
            promotionCallback = callback
            promotingSelection.isVisible = true
        }

        val table = Table(skin80).apply {
            add(Label(globalData.translate["game"], skin120)).colspan(3).left().top()
            row().padTop(100f)
            add(Label("${gameType.time}:00", skin)).left()
            add()
            add(Label("${gameType.time}:00", skin)).right()
            row()
            add(gameBoard).colspan(3).center()
            row()
            add(TextButton("undo", skin).apply {
                listener(Connection::undo)
            })
            add(TextButton("draw", skin).apply {
                listener(Connection::draw)
            })
            add(TextButton("surrender", skin).apply {
                listener(Connection::surrender)
            })
            setFillParent(true)
            debug = true
        }

        stage.addActor(table)

        promotingSelection = Table(skin80).apply {
            add(Label(globalData.translate["choose_piece"], skin120)).colspan(3).left().top()
            row().padTop(100f)

            val pieces =
                    if(gameBoard.isPlayingWhites) listOf(WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN)
                    else listOf(BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN)
            pieces.forEach {
                add(ImageButton(popupPieceTextures[it]).apply {
                    image.width = (BOARD_WIDTH/4).toFloat()
                    listener {
                        promotingSelection.isVisible = false
                        promotionCallback?.invoke(it)
                    }
                })
            }
            setFillParent(true)
            debug = true
            isVisible = false
        }

        stage.addActor(promotingSelection)

        Gdx.input.inputProcessor = stage
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
        stage.dispose()
        skin80.dispose()
        assetManager.dispose()
    }
}