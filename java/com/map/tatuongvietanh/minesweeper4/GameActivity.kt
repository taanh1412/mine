
package com.map.tatuongvietanh.minesweeper4

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.map.tatuongvietanh.minesweeper4.databinding.ActivityGameBinding
import com.map.tatuongvietanh.minesweeper4.model.Tile
import com.map.tatuongvietanh.minesweeper4.view.CellView
import com.map.tatuongvietanh.minesweeper4.viewmodel.GameViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val gameViewModel: GameViewModel by viewModels()

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
        gameViewModel.minefield.observe(this) { minefield ->
            updateGrid(minefield, calculateTileSize(minefield[0].size))
        }
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

        for (row in minefield) {
            for (tile in row) {
                val cellView = CellView(this).apply {
                    setTile(tile)
                    onCellClickListener = { row, col ->
                        gameViewModel.revealTile(row, col)
                    }
                    layoutParams = ViewGroup.LayoutParams(tileSize, tileSize)
                }
                binding.gridLayout.addView(cellView)
            }
        }
    }
}