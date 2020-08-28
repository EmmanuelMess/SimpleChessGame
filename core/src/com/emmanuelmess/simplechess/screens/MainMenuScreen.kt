package com.emmanuelmess.simplechess.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
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


class MainMenuScreen(
        private val globalData: GlobalData
): Screen(globalData) {
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
                fontFileName = "fonts/Roboto-Light.ttf"
                fontParameters.size = 120
            })

            load("roboto-80.ttf", BitmapFont::class.java, FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
                fontFileName = "fonts/Roboto-Light.ttf"
                fontParameters.size = 80
            })

            finishLoading()
        }

        skin80 = Skin().apply {
            add("default", assetManager.get<BitmapFont>("roboto-80.ttf"))
            addRegions(TextureAtlas(Gdx.files.internal("skin/skin.atlas")))
            load(Gdx.files.internal("skin/skin.skin"));
        }

        skin120 = Skin().apply {
            add("default", assetManager.get<BitmapFont>("roboto-120.ttf"))
            addRegions(TextureAtlas(Gdx.files.internal("skin/skin.atlas")))
            load(Gdx.files.internal("skin/skin.skin"));
        }

        stage = Stage(globalData.textViewport)

        val img: Texture = assetManager["icon/lichess grey.png"]
        stage.addActor(Container(Image(img)).apply {
            setFillParent(true)
        })

        val usernameText = TextField("", skin80)
        val passText = TextField("", skin80).apply {
            setPasswordCharacter('*')
            isPasswordMode = true
        }

        val table = Table(skin80).apply {
            add(Label(globalData.translate["name"], skin120)).padTop(100f).expandX().top()
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
        skin80.dispose()
        skin120.dispose()
        assetManager.dispose()
    }

}