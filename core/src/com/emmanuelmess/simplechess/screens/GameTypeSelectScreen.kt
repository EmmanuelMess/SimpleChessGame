package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.emmanuelmess.simplechess.game.GameCategory
import com.emmanuelmess.simplechess.game.GameType
import com.emmanuelmess.simplechess.GlobalData
import com.emmanuelmess.simplechess.Screen

class GameTypeSelectScreen(
        private val globalData: GlobalData
) : Screen(globalData) {
    private lateinit var assetManager: AssetManager
    private lateinit var stage: Stage
    private lateinit var skin80: Skin
    private lateinit var skin120: Skin

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
        stage = Stage(globalData.textViewport)

        val img: Texture = assetManager["icon/lichess grey.png"]
        stage.addActor(Container(Image(img)).apply {
            setFillParent(true)
        })

        val table = Table(skin80).apply {
            add(Label(globalData.translate["choose"], skin120)).padTop(100f).colspan(3).top()
            row()
            category(
                    "bullet",
                    GameType(null, 1, 0, GameCategory.BULLET),
                    GameType(null, 2, 1, GameCategory.BULLET),
                    GameType(null, 3, 0, GameCategory.BULLET)
            )
            category(
                    "blitz",
                    GameType(null, 3, 2, GameCategory.BLITZ),
                    GameType(null, 5, 0, GameCategory.BLITZ),
                    GameType(null, 5, 3, GameCategory.BLITZ)
            )
            category(
                    "rapid",
                    GameType(null, 10, 0, GameCategory.RAPID),
                    GameType(null, 10, 5, GameCategory.RAPID),
                    GameType(null, 15, 10, GameCategory.RAPID)
            )
            category(
                "classic",
                GameType(null, 30, 0, GameCategory.CLASSIC),
                GameType(null, 30, 20, GameCategory.CLASSIC)
            )
            row()
            /*TODO custom
            add(Label(globalData.translate["custom"], skin)).padTop(100f).left().colspan(3)
            row()
            add(TextButton("+", skin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        TODO()
                    }
                })
            })
            */
            setFillParent(true)
        }

        stage.addActor(table)

        Gdx.input.inputProcessor = stage
    }

    private fun Table.category(categoryName: String, a: GameType, b: GameType, c: GameType? = null) {
        row()
        add(Label(globalData.translate[categoryName], skin)).padTop(100f).left().colspan(3)
        row()
        add(buttonForGameType(a, skin))
        add(buttonForGameType(b, skin))

        if (c != null) {
            add(buttonForGameType(c, skin))
        }
    }

    private fun buttonForGameType(a: GameType, skin: Skin): TextButton {
        return TextButton(a.getString(), skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    globalData.changeScreen(GameScreen(globalData, a))
                }
            })
        }
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