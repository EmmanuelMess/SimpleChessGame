package com.emmanuelmess.simplechess

import com.badlogic.gdx.utils.Disposable

interface Screen: Disposable {
    fun create()
    fun resize(width: Int, height: Int)
    fun render()
}