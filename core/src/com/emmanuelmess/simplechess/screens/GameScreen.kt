package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.emmanuelmess.simplechess.*
import com.emmanuelmess.simplechess.game.GameBoard
import com.emmanuelmess.simplechess.game.GameType
import com.emmanuelmess.simplechess.server.Connection
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.*

class GameScreen(
        private val globalData: GlobalData,
        private val gameType: GameType
) : Screen(globalData) {
    private lateinit var assetManager: AssetManager
    private lateinit var stage: Stage
    private lateinit var skin80: Skin
    private lateinit var skin120: Skin
    private lateinit var pieceTextures: Map<Piece, Pixmap>

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

            load("images/pieces/bB.png", Pixmap::class.java)
            load("images/pieces/bK.png", Pixmap::class.java)
            load("images/pieces/bN.png", Pixmap::class.java)
            load("images/pieces/bP.png", Pixmap::class.java)
            load("images/pieces/bQ.png", Pixmap::class.java)
            load("images/pieces/bR.png", Pixmap::class.java)
            load("images/pieces/wB.png", Pixmap::class.java)
            load("images/pieces/wK.png", Pixmap::class.java)
            load("images/pieces/wN.png", Pixmap::class.java)
            load("images/pieces/wP.png", Pixmap::class.java)
            load("images/pieces/wQ.png", Pixmap::class.java)
            load("images/pieces/wR.png", Pixmap::class.java)

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
        stage = Stage(globalData.textViewport)

        val img: Texture = assetManager["icon/lichess grey.png"]
        stage.addActor(Container(Image(img)).apply {
            setFillParent(true)
        })

        val gameBoard = GameBoard(1200, pieceTextures)

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
                addListener(Connection::undo)
            })
            add(TextButton("draw", skin).apply {
                addListener(Connection::draw)
            })
            add(TextButton("surrender", skin).apply {
                addListener(Connection::surrender)
            })
            setFillParent(true)
            debug = true
        }

        stage.addActor(table)

        Gdx.input.inputProcessor = stage
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