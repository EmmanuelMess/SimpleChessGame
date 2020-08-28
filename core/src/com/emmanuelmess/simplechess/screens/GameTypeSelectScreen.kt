package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
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
    override fun create() {
        assetManager = AssetManager().apply {
            InternalFileHandleResolver().also {
                setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(it))
                setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(it))
            }

            finishLoading()
        }

        stage = Stage(globalData.textViewport)

        stage.addActor(Container(Image(globalData.lichessIcon)).apply {
            setFillParent(true)
        })

        val table = Table(globalData.skin80).apply {
            add(Label(globalData.translate["choose"], globalData.skin120)).padTop(100f).colspan(3).top()
            row()
            category(
                    GameCategory.BULLET,
                    GameType(1, 0, GameCategory.BULLET),
                    GameType(2, 1, GameCategory.BULLET),
                    GameType(3, 0, GameCategory.BULLET)
            )
            category(
                    GameCategory.BLITZ,
                    GameType(3, 2, GameCategory.BLITZ),
                    GameType(5, 0, GameCategory.BLITZ),
                    GameType(5, 3, GameCategory.BLITZ)
            )
            category(
                    GameCategory.RAPID,
                    GameType(10, 0, GameCategory.RAPID),
                    GameType(10, 5, GameCategory.RAPID),
                    GameType(15, 10, GameCategory.RAPID)
            )
            category(
                    GameCategory.CLASSIC,
                    GameType(30, 0, GameCategory.CLASSIC),
                    GameType(30, 20, GameCategory.CLASSIC)
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

    private fun Table.category(category: GameCategory, a: GameType, b: GameType, c: GameType? = null) {
        row()
        add(Label(globalData.translate[category.readableName], skin)).padTop(100f).left().colspan(3)
        row()
        add(buttonForGameType(a, skin)).height(200f).width(250f)
        add(buttonForGameType(b, skin)).height(200f).width(250f)

        if (c != null) {
            add(buttonForGameType(c, skin)).height(200f).width(250f)
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
        assetManager.dispose()
    }
}