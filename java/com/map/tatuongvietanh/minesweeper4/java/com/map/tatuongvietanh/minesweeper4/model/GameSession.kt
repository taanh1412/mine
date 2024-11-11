package com.map.tatuongvietanh.minesweeper4.model

import java.util.*

enum class GameStatus { ONGOING, WON, LOST, VIEW_ONLY }

class GameSession(
    val width: Int,
    val height: Int,
    val mineCount: Int
) {
    val minefield = Minefield(width, height, mineCount)
    var status: GameStatus = GameStatus.ONGOING
    val startTime: Date = Date()
    var endTime: Date? = null

    fun endSession(win: Boolean) {
        status = if (win) GameStatus.WON else GameStatus.LOST
        endTime = Date()
    }
}