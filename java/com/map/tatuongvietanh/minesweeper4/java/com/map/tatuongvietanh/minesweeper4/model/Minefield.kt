package com.map.tatuongvietanh.minesweeper4.model

class Minefield(val width: Int, val height: Int, val mineCount: Int) {
    val tiles: Array<Array<Tile>> = Array(height) { row ->
        Array(width) { col -> Tile(row, col) }
    }

    init {
        placeMines()
        calculateAdjacentMines()
    }

    private fun placeMines() {
        var placedMines = 0
        while (placedMines < mineCount) {
            val row = (0 until height).random()
            val col = (0 until width).random()
            if (!tiles[row][col].hasMine) {
                tiles[row][col].hasMine = true
                placedMines++
            }
        }
    }

    private fun calculateAdjacentMines() {
        for (row in 0 until height) {
            for (col in 0 until width) {
                if (!tiles[row][col].hasMine) {
                    tiles[row][col].adjacentMines = countAdjacentMines(row, col)
                }
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        val directions = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1, 0 to 1,
            1 to -1, 1 to 0, 1 to 1
        )
        return directions.count { (dr, dc) ->
            val newRow = row + dr
            val newCol = col + dc
            newRow in 0 until height && newCol in 0 until width && tiles[newRow][newCol].hasMine
        }
    }
}