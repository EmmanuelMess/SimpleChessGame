package com.emmanuelmess.simplechess

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.emmanuelmess.simplechess.screens.MainMenuScreen

class SimpleChessGame : ApplicationAdapter() {

    object Size {
        const val WIDTH = 1000f
        const val HEIGHT = 16 * WIDTH / 9

        const val C = 10f
    }

    private lateinit var globalAssetManager: AssetManager

    private lateinit var viewport: FillViewport
    private lateinit var camera: OrthographicCamera

    private lateinit var textViewport: FitViewport

    private lateinit var mainMenuScreen: MainMenuScreen

    override fun create() {
        Gdx.graphics.isContinuousRendering = false
        Gdx.graphics.requestRendering()

        camera = OrthographicCamera().apply {
            setToOrtho(true, Size.WIDTH, Size.HEIGHT)
        }
        viewport = FillViewport(Size.WIDTH, Size.HEIGHT, camera)

        textViewport = FitViewport(1440f, 2560f)

        globalAssetManager = AssetManager().apply {
            load("i18n/SimpleChess", I18NBundle::class.java)
            finishLoading()
        }

        mainMenuScreen = MainMenuScreen(textViewport, globalAssetManager.get("i18n/SimpleChess"))

        mainMenuScreen.create()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        mainMenuScreen.resize(width, height)
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT
                or if(Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)

        mainMenuScreen.render()
    }

    override fun dispose() {
        mainMenuScreen.dispose()
        globalAssetManager.dispose()
    }
}
