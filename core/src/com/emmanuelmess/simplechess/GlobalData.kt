package com.emmanuelmess.simplechess

import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport

data class GlobalData(
        val textViewport: FitViewport,
        val translate: I18NBundle,
        val changeScreen: (Screen) -> Unit,
        val viewport: FillViewport
)