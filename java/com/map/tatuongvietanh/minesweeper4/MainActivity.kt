package com.map.tatuongvietanh.minesweeper4

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.map.tatuongvietanh.minesweeper4.databinding.ActivityMainBinding
import com.map.tatuongvietanh.minesweeper4.model.GameStatus
import com.map.tatuongvietanh.minesweeper4.model.Tile
import com.map.tatuongvietanh.minesweeper4.view.CellView
import com.map.tatuongvietanh.minesweeper4.viewmodel.GameViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize data binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        // Difficulty selection buttons, launching GameActivity with appropriate settings
        binding.easyButton.setOnClickListener {
            startGameActivity(9, 9, 10) // Easy: 9x9 grid, 10 mines
        }
        binding.intermediateButton.setOnClickListener {
            startGameActivity(16, 16, 40) // Intermediate: 16x16 grid, 40 mines
        }
        binding.expertButton.setOnClickListener {
            startGameActivity(24, 24, 99) // Expert: 24x24 grid, 99 mines
        }


        // Continue button to resume the previous game
        binding.continueButton.setOnClickListener {
            startGameActivity(9, 9, 10) // Mocked continue; you would load saved game state here
        }

    }

    // Helper function to start GameActivity with difficulty parameters
    private fun startGameActivity(gridWidth: Int, gridHeight: Int, mineCount: Int) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("GRID_WIDTH", gridWidth)
            putExtra("GRID_HEIGHT", gridHeight)
            putExtra("MINE_COUNT", mineCount)
        }
        startActivity(intent)
    }
}


