package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.emmanuelmess.simplechess.GlobalData
import com.emmanuelmess.simplechess.Screen

/**
 * TODO implement connection to lichess
 */
class MainMenuScreen(
        private val globalData: GlobalData
): Screen(globalData) {
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

        val usernameText = TextField("", globalData.skin)
        val passText = TextField("", globalData.skin).apply {
            setPasswordCharacter('*')
            isPasswordMode = true
        }

        val table = Table(globalData.skin).apply {
            add(Label(globalData.translate["name"], skin, "default120", Color.BLACK)).padTop(100f).expandX().top()
            row()
            add(Label(globalData.translate["username"], skin)).padTop(100f).left().expandX()
            row()
            add(usernameText).fillX().left()
            row()
            add(Label(globalData.translate["password"], skin)).padTop(100f).left().expandX()
            row()
            add(passText).fillX().left()
            row()
            add(TextButton(globalData.translate["login"], skin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        globalData.changeScreen(GameTypeSelectScreen(globalData))

                        /*TODO
                        globalData.connection.logIn(usernameText.text, passText.text, {
                            globalData.changeScreen(GameTypeSelectScreen(globalData))
                        }, {
                            TODO()
                        })
                        */
                    }
                })
            }).padTop(100f)

            setFillParent(true)
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
        assetManager.dispose()
    }

}