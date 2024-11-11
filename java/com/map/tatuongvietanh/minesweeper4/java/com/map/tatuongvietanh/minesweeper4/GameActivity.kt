package com.map.tatuongvietanh.minesweeper4

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.map.tatuongvietanh.minesweeper4.databinding.ActivityGameBinding
import com.map.tatuongvietanh.minesweeper4.model.GameStatus
import com.map.tatuongvietanh.minesweeper4.model.Tile
import com.map.tatuongvietanh.minesweeper4.view.CellView
import com.map.tatuongvietanh.minesweeper4.viewmodel.GameViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val gameViewModel: GameViewModel by viewModels()
    private var isViewOnlyMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize data binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        // Get game settings from intent extras
        val gridWidth = intent.getIntExtra("GRID_WIDTH", 9)
        val gridHeight = intent.getIntExtra("GRID_HEIGHT", 9)
        val mineCount = intent.getIntExtra("MINE_COUNT", 10)

        // Start the game with provided settings
        gameViewModel.startGame(gridWidth, gridHeight, mineCount)

        // Set up back button
        binding.backButton.setOnClickListener {
            finish() // Return to the main menu
        }

        // Adjust the grid to fit the screen for the specified grid dimensions
        setupGrid(gridWidth)
    }

    private fun setupObservers() {
        // Observe minefield changes and update the grid
        gameViewModel.minefield.observe(this) { minefield ->
            updateGrid(minefield, calculateTileSize(minefield[0].size))
        }

        // Observe game status to show end-game dialog
        gameViewModel.gameStatus.observe(this) { status ->
            when (status) {
                GameStatus.WON -> showEndGameDialog(true)
                GameStatus.LOST -> showEndGameDialog(false)
                else -> {} // Do nothing if ongoing or view-only
            }
        }

        // Observe time elapsed and update the timer display
        gameViewModel.timeElapsed.observe(this) { time ->
            binding.timerTextView.text = formatTime(time)
        }
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun setupGrid(columnCount: Int) {
        binding.gridLayout.columnCount = columnCount

        // Calculate tile size based on screen width to make the grid fit centrally
        val tileSize = calculateTileSize(columnCount)
        updateGrid(gameViewModel.minefield.value ?: arrayOf(), tileSize)
    }

    private fun calculateTileSize(columnCount: Int): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Set a maximum grid width (e.g., 80% of screen width)
        val maxGridWidth = (screenWidth * 0.8).toInt()
        val padding = 32 // Total padding on the sides (16dp each side)

        // Calculate the tile size to fit within the maximum grid width
        return (maxGridWidth - padding) / columnCount
    }

    private fun updateGrid(minefield: Array<Array<Tile>>, tileSize: Int) {
        binding.gridLayout.removeAllViews() // Clear previous views if any

        for ((rowIndex, row) in minefield.withIndex()) {
            for ((colIndex, tile) in row.withIndex()) {
                val cellView = CellView(this).apply {
                    setTile(tile)
                    onCellClickListener = { r, c ->
                        if (!isViewOnlyMode) {
                            gameViewModel.revealTile(r, c)
                        } else {
                            Toast.makeText(this@GameActivity, "View-only mode enabled", Toast.LENGTH_SHORT).show()
                        }
                    }
                    layoutParams = ViewGroup.LayoutParams(tileSize, tileSize)
                }
                binding.gridLayout.addView(cellView)
            }
        }
    }

    private fun showEndGameDialog(isWin: Boolean) {
        val message = if (isWin) "Congratulations! You won the game." else "Game Over! You hit a mine."

        AlertDialog.Builder(this)
            .setTitle("Game Result")
            .setMessage(message)
            .setPositiveButton("Restart") { _, _ ->
                restartGame()
            }
            .setNegativeButton("Confirm") { _, _ ->
                enterViewOnlyMode()
            }
            .setCancelable(false)
            .show()
    }

    private fun restartGame() {
        isViewOnlyMode = false
        gameViewModel.restartGame()
        setupGrid(gameViewModel.minefield.value?.size ?: 9)
    }

    private fun enterViewOnlyMode() {
        isViewOnlyMode = true
        gameViewModel.enterViewOnlyMode() // Update ViewModel to set view-only status if needed
    }
}
