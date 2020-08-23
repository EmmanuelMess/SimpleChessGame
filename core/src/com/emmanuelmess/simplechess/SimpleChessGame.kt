package com.emmanuelmess.simplechess

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.emmanuelmess.simplechess.screens.MainMenuScreen

class SimpleChessGame : ApplicationAdapter() {

    object Size {
        const val WIDTH = 1440f
        const val HEIGHT = 2560f

        const val C = 10f
    }

    private lateinit var globalAssetManager: AssetManager

    private lateinit var viewport: FillViewport
    private lateinit var camera: OrthographicCamera

    private lateinit var textViewport: FitViewport

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
            load("i18n/SimpleChess", I18NBundle::class.java)
            finishLoading()
        }

        changeScreen(MainMenuScreen(
                GlobalData(
                        textViewport,
                        globalAssetManager["i18n/SimpleChess"],
                        this::changeScreen,
                        viewport
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
