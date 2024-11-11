package com.map.tatuongvietanh.minesweeper4.model

data class Tile(
    val row: Int,
    val column: Int,
    var hasMine: Boolean = false,
    var isRevealed: Boolean = false,
    var isFlagged: Boolean = false,
    var adjacentMines: Int = 0
)