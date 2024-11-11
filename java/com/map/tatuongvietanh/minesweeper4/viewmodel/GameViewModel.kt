package com.map.tatuongvietanh.minesweeper4.viewmodel

// GameViewModel.kt
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.map.tatuongvietanh.minesweeper4.model.GameSession
import com.map.tatuongvietanh.minesweeper4.model.GameStatus
import com.map.tatuongvietanh.minesweeper4.model.Tile
import java.util.*

class GameViewModel : ViewModel() {

    private lateinit var currentSession: GameSession
    private val _gameStatus = MutableLiveData(GameStatus.ONGOING)
    val gameStatus: LiveData<GameStatus> get() = _gameStatus

    private val _timeElapsed = MutableLiveData<Long>()
    val timeElapsed: LiveData<Long> get() = _timeElapsed

    private val _minefield = MutableLiveData<Array<Array<Tile>>>()
    val minefield: LiveData<Array<Array<Tile>>> get() = _minefield

    private var timer: CountDownTimer? = null

    fun startGame(width: Int, height: Int, mineCount: Int) {
        currentSession = GameSession(width, height, mineCount)
        _minefield.value = currentSession.minefield.tiles
        _gameStatus.value = GameStatus.ONGOING
        startTimer()
    }

    private fun startTimer() {
        _timeElapsed.value = 0L
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeElapsed.value = (_timeElapsed.value ?: 0) + 1
            }

            override fun onFinish() {
                // This will never finish since we set Long.MAX_VALUE
            }
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun revealTile(row: Int, col: Int) {
        val tile = currentSession.minefield.tiles[row][col]
        if (tile.isRevealed || _gameStatus.value != GameStatus.ONGOING) return

        tile.isRevealed = true
        _minefield.value = currentSession.minefield.tiles

        if (tile.hasMine) {
            endGame(false)
        } else if (tile.adjacentMines == 0) {
            revealSurroundingTiles(row, col)
        }

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
                if (!adjacentTile.isRevealed && !adjacentTile.hasMine) {
                    revealTile(newRow, newCol)
                }
            }
        }
    }

    private fun checkWinCondition(): Boolean {
        return currentSession.minefield.tiles.flatten().all { tile ->
            tile.isRevealed || tile.hasMine
        }
    }

    private fun endGame(win: Boolean) {
        stopTimer()
        currentSession.endSession(win)
        _gameStatus.value = if (win) GameStatus.WON else GameStatus.LOST
    }

    fun restartGame() {
        startGame(currentSession.width, currentSession.height, currentSession.mineCount)
    }
}