package com.map.tatuongvietanh.minesweeper4.model

import java.util.*

data class Score(
    val sessionId: UUID = UUID.randomUUID(),
    val timeTaken: Long,   // In seconds
    val boardSize: String,
    val mineCount: Int,
    val date: Date = Date(),
    val result: GameStatus
)