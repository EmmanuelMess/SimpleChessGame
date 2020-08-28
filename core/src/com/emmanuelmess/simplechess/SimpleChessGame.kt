package com.emmanuelmess.simplechess

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.emmanuelmess.simplechess.net.Networking
import com.emmanuelmess.simplechess.screens.MainMenuScreen

class SimpleChessGame(
        val networking: Networking
) : ApplicationAdapter() {

    object Size {
        const val WIDTH = 1440f
        const val HEIGHT = 2560f

        const val C = 10f
    }

    private lateinit var globalAssetManager: AssetManager

    private lateinit var viewport: FillViewport
    private lateinit var camera: OrthographicCamera

    private lateinit var textViewport: FitViewport

    private lateinit var skin80: Skin
    private lateinit var skin120: Skin

    private var currentScreen: Screen? = null

    override fun create() {
        Gdx.graphics.isContinuousRendering = false
        Gdx.graphics.requestRendering()

        camera = OrthographicCamera().apply {
            setToOrtho(true, Size.WIDTH, Size.HEIGHT)
        }
        viewport = FillViewport(Size.WIDTH, Size.HEIGHT, camera)

        textViewport = FitViewport(Size.WIDTH, Size.HEIGHT)

        globalAssetManager = AssetManager().apply {
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

            load("i18n/SimpleChess", I18NBundle::class.java)

            finishLoading()
        }

        skin80 = Skin().apply {
            add("default", globalAssetManager["roboto-80.ttf"])
            addRegions(TextureAtlas(Gdx.files.internal("skin/skin.atlas")))
            load(Gdx.files.internal("skin/skin.skin"));
        }

        skin120 = Skin().apply {
            add("default", globalAssetManager["roboto-120.ttf"])
            addRegions(TextureAtlas(Gdx.files.internal("skin/skin.atlas")))
            load(Gdx.files.internal("skin/skin.skin"));
        }

        changeScreen(MainMenuScreen(
                GlobalData(
                        textViewport,
                        globalAssetManager["i18n/SimpleChess"],
                        globalAssetManager["icon/lichess grey.png"],
                        skin80,
                        skin120,
                        this::changeScreen,
                        viewport,
                        networking
                )
        ))
    }

    fun changeScreen(screen: Screen) {
        currentScreen?.dispose()
        screen.create()
        screen.resize(viewport.screenWidth, viewport.screenHeight)
        currentScreen = screen
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        currentScreen?.resize(width, height)
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT
                or if(Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)

        currentScreen?.render()
    }

    override fun dispose() {
        currentScreen?.dispose()
        globalAssetManager.dispose()
    }
}
