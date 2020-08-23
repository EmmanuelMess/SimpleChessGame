package com.emmanuelmess.simplechess

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FitViewport

abstract class Screen(
        private val textViewport: FitViewport,
        private val translate: I18NBundle,
        private val changeScreen: (Screen) -> Unit
): Disposable {
    abstract fun create()
    abstract fun resize(width: Int, height: Int)
    abstract fun render()
}