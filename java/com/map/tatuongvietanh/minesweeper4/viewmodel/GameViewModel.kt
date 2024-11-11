package com.map.tatuongvietanh.minesweeper4.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.map.tatuongvietanh.minesweeper4.model.GameSession
import com.map.tatuongvietanh.minesweeper4.model.GameStatus
import com.map.tatuongvietanh.minesweeper4.model.Tile

class GameViewModel : ViewModel() {

    private lateinit var currentSession: GameSession
    private val _gameStatus = MutableLiveData(GameStatus.ONGOING)
    val gameStatus: LiveData<GameStatus> get() = _gameStatus

    private val _timeElapsed = MutableLiveData<Long>()
    val timeElapsed: LiveData<Long> get() = _timeElapsed

    private val _minefield = MutableLiveData<Array<Array<Tile>>>()
    val minefield: LiveData<Array<Array<Tile>>> get() = _minefield

    private val _remainingMines = MutableLiveData<Int>() // Mine counter
    val remainingMines: LiveData<Int> get() = _remainingMines

    private val _score = MutableLiveData(0) // Score counter
    val score: LiveData<Int> get() = _score

    private var timer: CountDownTimer? = null

    fun startGame(width: Int, height: Int, mineCount: Int) {
        currentSession = GameSession(width, height, mineCount)
        _minefield.value = currentSession.minefield.tiles
        _remainingMines.value = mineCount
        _gameStatus.value = GameStatus.ONGOING
        _score.value = 0 // Reset score at game start
        startTimer()
    }

    private fun startTimer() {
        _timeElapsed.value = 0L
        timer?.cancel() // Cancel any existing timer before starting a new one
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeElapsed.value = (_timeElapsed.value ?: 0) + 1
            }

            override fun onFinish() {}
        }
        timer?.start()
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun revealTile(row: Int, col: Int) {
        val tile = currentSession.minefield.tiles[row][col]
        // If the tile is already revealed or the game has ended, do nothing
        if (tile.isRevealed || _gameStatus.value != GameStatus.ONGOING) return

        // Reveal the clicked tile
        tile.isRevealed = true
        _minefield.value = currentSession.minefield.tiles

        // Add points for revealing a tile without a mine
        _score.value = (_score.value ?: 0) + calculatePoints(tile)

        // If it's a mine, end the game with a loss
        if (tile.hasMine) {
            endGame(false)
            return
        }
        // If there are no adjacent mines, recursively reveal surrounding tiles
        else if (tile.adjacentMines == 0) {
            revealSurroundingTiles(row, col)
        }

        // Check if the game is won
        if (checkWinCondition()) {
            endGame(true)
        }
    }

    private fun revealSurroundingTiles(row: Int, col: Int) {
        val directions = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1, 0 to 1,
            1 to -1, 1 to 0, 1 to 1
        )
        for ((dr, dc) in directions) {
            val newRow = row + dr
            val newCol = col + dc
            if (newRow in 0 until currentSession.minefield.height &&
                newCol in 0 until currentSession.minefield.width
            ) {
                val adjacentTile = currentSession.minefield.tiles[newRow][newCol]
                // Only reveal non-mine, non-flagged, unrevealed tiles
                if (!adjacentTile.isRevealed && !adjacentTile.hasMine) {
                    revealTile(newRow, newCol) // Recursively reveal
                }
            }
        }
    }

    fun toggleFlag(row: Int, col: Int) {
        val tile = currentSession.minefield.tiles[row][col]
        if (!tile.isRevealed) {
            tile.isFlagged = !tile.isFlagged
            if (tile.isFlagged) {
                _remainingMines.value = (_remainingMines.value ?: 0) - 1
                _score.value = (_score.value ?: 0) + 10 // Add points for placing a flag
            } else {
                _remainingMines.value = (_remainingMines.value ?: 0) + 1
                _score.value = (_score.value ?: 0) - 10 // Deduct points for removing a flag
            }
            _minefield.value = currentSession.minefield.tiles // Update UI
        }
    }

    private fun calculatePoints(tile: Tile): Int {
        return when {
            tile.hasMine -> 0 // No points for mines
            tile.adjacentMines == 0 -> 20 // Higher points for revealing empty tiles
            else -> 10 // Lower points for tiles with adjacent mines
        }
    }

    private fun checkWinCondition(): Boolean {
        return currentSession.minefield.tiles.flatten().all { tile ->
            tile.isRevealed || tile.hasMine
        }
    }

    private fun endGame(win: Boolean) {
        stopTimer() // Stop the timer when the game ends
        if (win) _score.value = (_score.value ?: 0) + 100 // Bonus points for winning
        _gameStatus.value = if (win) GameStatus.WON else GameStatus.LOST
    }

    fun restartGame() {
        startGame(currentSession.width, currentSession.height, currentSession.mineCount)
    }

    fun enterViewOnlyMode() {
        _gameStatus.value = GameStatus.VIEW_ONLY
    }
}
