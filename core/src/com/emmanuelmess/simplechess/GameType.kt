package com.emmanuelmess.simplechess

enum class GameCategory {
    BULLET, BLITZ, RAPID, CLASSIC, CUSTOM
}

data class GameType(val name: String?, val time: Int, val timeAdded: Int, val category: GameCategory) {
    fun getString() = name ?: "$time+$timeAdded"
}