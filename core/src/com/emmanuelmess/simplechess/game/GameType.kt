package com.emmanuelmess.simplechess.game

enum class GameCategory(public val readableName: String) {
    BULLET("bullet"),
    BLITZ("blitz"),
    RAPID("rapid"),
    CLASSIC("classic"),
    CUSTOM("custom")
}

data class GameType(val time: Int, val timeAdded: Long, val category: GameCategory) {
    fun getString() = "$time+$timeAdded"
}