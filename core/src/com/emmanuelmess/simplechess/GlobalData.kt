package com.emmanuelmess.simplechess

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.emmanuelmess.simplechess.net.Networking

data class GlobalData(
        val textViewport: FitViewport,
        val translate: I18NBundle,
        val lichessIcon: Texture,
        val skin80:  Skin,
        val skin120: Skin,
        val changeScreen: (Screen) -> Unit,
        val viewport: FillViewport,
        val connection: Networking
)